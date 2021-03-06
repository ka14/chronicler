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

package com.github.fsanaulla.chronicler.urlhttp.shared

import java.net.HttpURLConnection

import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient.CustomizationF
import com.softwaremill.sttp.{SttpBackend, TryHttpURLConnectionBackend}

import scala.util.Try

class InfluxUrlClient(customization: Option[CustomizationF]) { self: AutoCloseable =>

  private[urlhttp] implicit val backend: SttpBackend[Try, Nothing] =
    customization.fold(TryHttpURLConnectionBackend())(cust => TryHttpURLConnectionBackend(customizeConnection = cust))

  def close(): Unit = backend.close()
}

object InfluxUrlClient {
  type CustomizationF = HttpURLConnection => Unit
}
