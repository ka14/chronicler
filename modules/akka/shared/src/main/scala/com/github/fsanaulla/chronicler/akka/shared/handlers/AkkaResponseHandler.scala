/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.akka.shared.handlers

import _root_.akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.shared.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.typeclasses.ResponseHandler
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] final class AkkaResponseHandler(implicit mat: ActorMaterializer,
                                              implicit val ec: ExecutionContext)
  extends AkkaJsonHandler with ResponseHandler[Future, HttpResponse]  {

  // Simply result's
  private[chronicler]
  override def toResult(response: HttpResponse): Future[WriteResult] = {
    response.status.intValue() match {
      case code if isSuccessful(code) && code != 204 =>
        getOptResponseError(response) map {
          case Some(msg) =>
            WriteResult.failed(code, new OperationException(msg))
          case _ =>
            WriteResult.successful(code)
        }
      case 204 =>
        WriteResult.successfulFuture(204)
      case other =>
        errorHandler(response, other)
          .map(ex => WriteResult.failed(other, ex))
    }
  }

  private[chronicler]
  override def toComplexQueryResult[A: ClassTag, B: ClassTag](response: HttpResponse,
                                                              buildF: (String, Array[A]) => B)
                                                             (implicit reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptInfluxInfo[A])
          .map {
            case Some(arr) =>
              QueryResult.successful[B](
                code,
                arr.map { case (dbName, result) => buildF(dbName, result) }
              )
            case _ =>
              QueryResult.empty[B](code)
          }
      case other =>
        queryErrorHandler[B](response, other)
    }
  }

  private[chronicler]
  override def toQueryJsResult(response: HttpResponse): Future[QueryResult[JArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptQueryResult)
          .map {
            case Some(seq) =>
              QueryResult.successful[JArray](code, seq)
            case _ =>
              QueryResult.empty[JArray](code)}
      case other =>
        queryErrorHandler[JArray](response, other)
    }
  }

  private[chronicler]
  override def toGroupedJsResult(response: HttpResponse): Future[GroupedResult[JArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptGropedResult)
          .map {
            case Some(arr) => GroupedResult.successful[JArray](code, arr)
            case _ => GroupedResult.empty[JArray](code)}
      case other =>
        errorHandler(response, other)
          .map(ex => GroupedResult.failed[JArray](other, ex))
    }
  }

  private[chronicler]
  override def toBulkQueryJsResult(response: HttpResponse): Future[QueryResult[Array[JArray]]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptBulkInfluxPoints)
          .map {
            case Some(seq) => QueryResult.successful[Array[JArray]](code, seq)
            case _ => QueryResult.empty[Array[JArray]](code)
          }
      case other =>
        queryErrorHandler[Array[JArray]](response, other)
    }
  }

  private[chronicler]
  override def toQueryResult[A: ClassTag](response: HttpResponse)
                                         (implicit reader: InfluxReader[A]): Future[QueryResult[A]] =
    toQueryJsResult(response).map(_.map(reader.read))

  private[chronicler]
  override def errorHandler(response: HttpResponse,
                            code: Int): Future[InfluxException] = code match {
    case 400 =>
      getResponseError(response).map(errMsg => new BadRequestException(errMsg))
    case 401 =>
      getResponseError(response).map(errMsg => new AuthorizationException(errMsg))
    case 404 =>
      getResponseError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 =>
      getResponseError(response).map(errMsg => new InternalServerError(errMsg))
    case _ =>
      getResponseError(response).map(errMsg => new UnknownResponseException(errMsg))
  }

  private[this] def queryErrorHandler[A: ClassTag](response: HttpResponse,
                                                   code: Int): Future[QueryResult[A]] =
    errorHandler(response, code).map(ex => QueryResult.failed[A](code, ex))
}
