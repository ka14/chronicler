package com.github.fsanaulla.chronicler.ahc.io.it

import com.github.fsanaulla.chronicler.ahc.io.{AhcIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.NoContentResult
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.09.17
  */
class SystemManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB with Futures {

  lazy val influx: AhcIOClient =
    InfluxIO(host, port, Some(creds))

  it should "ping InfluxDB" in {
    influx.ping.futureValue shouldEqual NoContentResult
  }
}
