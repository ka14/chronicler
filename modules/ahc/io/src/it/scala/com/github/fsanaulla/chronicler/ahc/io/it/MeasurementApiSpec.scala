package com.github.fsanaulla.chronicler.ahc.io.it

import com.github.fsanaulla.chronicler.ahc.io.api.Measurement
import com.github.fsanaulla.chronicler.ahc.io.{AhcIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.ahc.management.{AhcManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.ahc.shared.InfluxConfig
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, FakeEntity, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementApiSpec extends FlatSpec with Matchers with Futures with DockerizedInfluxDB {

  val safeDB = "db"
  val measName = "meas"

  lazy val influxConf =
    InfluxConfig(host, port, credentials = Some(creds), gzipped = false, None)

  lazy val mng: AhcManagementClient = InfluxMng(influxConf)
  lazy val io: AhcIOClient = InfluxIO(influxConf)

  lazy val meas: Measurement[FakeEntity] = io.measurement[FakeEntity](safeDB, measName)

  it should "make single write" in {
    mng.createDatabase(safeDB).futureValue shouldEqual OkResult

    meas.write(singleEntity).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult shouldEqual Array(singleEntity)
  }

  it should "make safe bulk write" in {
    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult

    meas.read(s"SELECT * FROM $measName")
      .futureValue
      .queryResult
      .length shouldEqual 3

    mng.close() shouldEqual {}
    io.close() shouldEqual {}
  }
}
