package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.model.{ShardGroupsInfo, ShardInfo}
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers, TryValues}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class ShardManagementSpec extends FlatSpec with Matchers with DockerizedInfluxDB with TryValues {

  val testDb = "_internal"

  lazy val influx: UrlManagementClient =
    InfluxMng.apply(host, port, Some(creds))

  "shard operations" should "show shards" in {

    influx.createDatabase(testDb, shardDuration = Some("1s")).success.value shouldEqual OkResult

    val shards = influx.showShards.success.value.queryResult

    shards should not be Array.empty[ShardInfo]
  }

  it should "show shards groupe" in {

    val shardGroups = influx.showShardGroups.success.value.queryResult

    shardGroups should not equal Array.empty[ShardGroupsInfo]

    shardGroups shouldBe a [Array[_]]

    shardGroups.head shouldBe a [ShardGroupsInfo]

    influx.close() shouldEqual {}
  }
}
