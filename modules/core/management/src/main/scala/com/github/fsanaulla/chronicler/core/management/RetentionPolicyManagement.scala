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

package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.RetentionPolicyManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait RetentionPolicyManagement[F[_], Req, Resp, Uri, Entity] extends RetentionPolicyManagementQuery[Uri] {
  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Req, Resp, Uri]
  implicit val rh: ResponseHandler[F, Resp]
  implicit val fm: FlatMap[F]

  import re.buildRequest

  /**
    * Create retention policy for specified database
    * @param rpName        - retention policy name
    * @param dbName        - database name
    * @param duration      - retention policy duration
    * @param replication   - replication factor
    * @param shardDuration - shard duration value
    * @param default       - use default
    * @return              - execution result
    */
  final def createRetentionPolicy(rpName: String,
                                  dbName: String,
                                  duration: String,
                                  replication: Int = 1,
                                  shardDuration: Option[String] = None,
                                  default: Boolean = false): F[WriteResult] = {
    require(replication > 0, "Replication must greater that 0")
    fm.flatMap(re.execute(createRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default)))(rh.toResult)
  }

  /** Update retention policy */
  final def updateRetentionPolicy(rpName: String,
                                  dbName: String,
                                  duration: Option[String] = None,
                                  replication: Option[Int] = None,
                                  shardDuration: Option[String] = None,
                                  default: Boolean = false): F[WriteResult] =
    fm.flatMap(re.execute(updateRetentionPolicyQuery(rpName, dbName, duration, replication, shardDuration, default)))(rh.toResult)


  /** Drop retention policy */
  final def dropRetentionPolicy(rpName: String, dbName: String): F[WriteResult] =
    fm.flatMap(re.execute(dropRetentionPolicyQuery(rpName, dbName)))(rh.toResult)

  /** Show list of retention polices */
  final def showRetentionPolicies(dbName: String): F[QueryResult[RetentionPolicyInfo]] =
    fm.flatMap(re.execute(showRetentionPoliciesQuery(dbName)))(rh.toQueryResult[RetentionPolicyInfo])

}
