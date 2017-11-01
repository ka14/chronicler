package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.api.Measurement
import com.github.fsanaulla.clients.InfluxHttpClient
import com.github.fsanaulla.utils.SampleEntitys._
import com.github.fsanaulla.utils.TestHelper.{FakeEntity, _}
import com.github.fsanaulla.utils.TestSpec
import org.scalatest.BeforeAndAfterAll

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.09.17
  */
class MeasurementSpec extends TestSpec with BeforeAndAfterAll {

  override def afterAll: Unit = {

    influx.dropDatabase(safeDB)

    influx.close()
  }

  val safeDB = "meas_db"

  val measName = "meas"

  // INIT INFLUX CLIENT
  lazy val influx: InfluxHttpClient = InfluxClientsFactory.createHttpClient(
      host = influxHost,
      username = credentials.username,
      password = credentials.password
  )

  lazy val meas: Measurement[FakeEntity] = influx.measurement[FakeEntity](safeDB, measName)


  "Safe entity" should "init env" in {

    influx.createDatabase(safeDB).futureValue shouldEqual OkResult
  }

  it should "make safe single write" in {

    meas.write(singleEntity).futureValue shouldEqual NoContentResult
  }

  it should "make safe bulk write" in {

    meas.bulkWrite(multiEntitys).futureValue shouldEqual NoContentResult
  }
}
