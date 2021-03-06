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

package com.github.fsanaulla.chronicler.akka.io.api

import java.io.File

import _root_.akka.http.scaladsl.model.RequestEntity
import com.github.fsanaulla.chronicler.akka.io.models.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.io.serializers._
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class Database(dbName: String, gzipped: Boolean)
                    (implicit ex: ExecutionContext, wr: AkkaWriter, rd: AkkaReader)
  extends DatabaseApi[Future, RequestEntity] with Serializable[RequestEntity] {

  override def writeFromFile(file: File,
                             consistency: Option[Consistency] = None,
                             precision: Option[Precision] = None,
                             retentionPolicy: Option[String] = None): Future[WriteResult] =
    wr.writeFromFile(dbName, file, consistency, precision, retentionPolicy, gzipped)

  override def writeNative(point: String,
                           consistency: Option[Consistency] = None,
                           precision: Option[Precision] = None,
                           retentionPolicy: Option[String] = None): Future[WriteResult] =
    wr.writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  override def bulkWriteNative(points: Seq[String],
                               consistency: Option[Consistency] = None,
                               precision: Option[Precision] = None,
                               retentionPolicy: Option[String] = None): Future[WriteResult] =
    wr.writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  override def writePoint(point: Point,
                          consistency: Option[Consistency] = None,
                          precision: Option[Precision] = None,
                          retentionPolicy: Option[String] = None): Future[WriteResult] =
    wr.writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  override def bulkWritePoints(points: Seq[Point],
                               consistency: Option[Consistency] = None,
                               precision: Option[Precision] = None,
                               retentionPolicy: Option[String] = None): Future[WriteResult] =
    wr.writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  override def readJs(query: String,
                      epoch: Option[Epoch] = None,
                      pretty: Boolean = false,
                      chunked: Boolean = false): Future[ReadResult[JArray]] =
    rd.readJs(dbName, query, epoch, pretty, chunked)

  override def bulkReadJs(queries: Seq[String],
                          epoch: Option[Epoch] = None,
                          pretty: Boolean = false,
                          chunked: Boolean = false): Future[QueryResult[Array[JArray]]] =
    rd.bulkReadJs(dbName, queries, epoch, pretty, chunked)


  override def read[A: ClassTag](query: String,
                                 epoch: Option[Epoch] = None,
                                 pretty: Boolean = false,
                                 chunked: Boolean = false)
                                (implicit reader: InfluxReader[A]): Future[ReadResult[A]] =
    readJs(query, epoch, pretty, chunked).map(_.map(reader.read))
}
