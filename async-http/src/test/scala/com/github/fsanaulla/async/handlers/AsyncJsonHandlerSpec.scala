package com.github.fsanaulla.async.handlers

import com.github.fsanaulla.chronicler.async.handlers.AsyncJsonHandler
import com.github.fsanaulla.core.test.utils.TestSpec
import com.softwaremill.sttp.Response
import spray.json.{JsObject, JsonParser}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class AsyncJsonHandlerSpec extends TestSpec with AsyncJsonHandler {

  override implicit val ex: ExecutionContext = ExecutionContext.Implicits.global

  val singleStrJson = """{
                      "results": [
                          {
                              "statement_id": 0,
                              "series": [
                                 {
                                      "name": "cpu_load_short",
                                      "columns": [
                                          "time",
                                          "value"
                                      ],
                                      "values": [
                                          [
                                              "2015-01-29T21:55:43.702900257Z",
                                              2
                                          ],
                                          [
                                              "2015-01-29T21:55:43.702900257Z",
                                              0.55
                                          ],
                                          [
                                              "2015-06-11T20:46:02Z",
                                              0.64
                                          ]
                                      ]
                                  }
                              ]
                          }
                      ]
                  }"""

  val multipleJsonStr =
 """{
     "results": [
         {
             "statement_id": 0,
             "series": [
                 {
                     "name": "cpu_load_short",
                     "columns": [
                         "time",
                         "value"
                     ],
                     "values": [
                         [
                             "2015-01-29T21:55:43.702900257Z",
                             2
                         ],
                         [
                             "2015-01-29T21:55:43.702900257Z",
                             0.55
                         ],
                         [
                             "2015-06-11T20:46:02Z",
                             0.64
                         ]
                     ]
                 }
             ]
         },
         {
             "statement_id": 1,
             "series": [
                 {
                     "name": "cpu_load_short",
                     "columns": [
                         "time",
                         "count"
                     ],
                     "values": [
                         [
                             "1970-01-01T00:00:00Z",
                             3
                         ]
                     ]
                 }
             ]
         }
     ]
 }"""

  val singleHttpResponse = Response(body = Right(JsonParser(singleStrJson).asJsObject), 200, "", Nil, Nil)

  val jsResult: JsObject = JsonParser(singleStrJson).asJsObject

  "Async json handler" should "extract js object from HTTP response" in {
    getJsBody(singleHttpResponse).futureValue shouldEqual jsResult
  }

  it should "extract multiple js query result from HTTP response" in {
    val jsObject = JsonParser(multipleJsonStr).asJsObject

    val res = getBulkInfluxValue(jsObject)

    res.size shouldEqual 2
    res.head should not equal Nil
    res.last should not equal Nil

  }
}