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

package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, WriteResult}
import com.github.fsanaulla.chronicler.urlhttp.io.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.urlhttp.io.models.{UrlReader, UrlWriter}
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient.CustomizationF
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor, UrlResponseHandler}

import scala.reflect.ClassTag
import scala.util.Try

final class UrlIOClient(host: String,
                        port: Int,
                        credentials: Option[InfluxCredentials],
                        gzipped: Boolean,
                        customization: Option[CustomizationF])
  extends InfluxUrlClient(customization) with IOClient[Try, String] {

  implicit val qb: UrlQueryBuilder = new UrlQueryBuilder(host, port, credentials)
  implicit val re: UrlRequestExecutor = new UrlRequestExecutor
  implicit val rh: UrlResponseHandler = new UrlResponseHandler
  implicit val wr: UrlWriter = new UrlWriter
  implicit val rd: UrlReader = new UrlReader

  override def database(dbName: String): Database =
    new Database(dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, gzipped)

  override def ping: Try[WriteResult] =
    re
      .execute(re.buildRequest(qb.buildQuery("/ping", Map.empty[String, String])))
      .flatMap(rh.toResult)
}
