package com.github.fsanaulla.core.client

import com.github.fsanaulla.core.api.management._
import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model.HasCredentials

private[fsanaulla] trait InfluxClient[R, U, M, E]
  extends SystemManagement[E]
    with DatabaseManagement[R, U, M, E]
    with UserManagement[R, U, M, E]
    with RetentionPolicyManagement[R, U, M, E]
    with ContinuousQueryManagement[R, U, M, E]
    with ShardManagement[R, U, M, E]
    with SubscriptionManagement[R, U, M, E] {
  self: RequestHandler[R, U, M, E] with ResponseHandler[R] with QueryHandler[U] with HasCredentials =>

  /**
    * Close HTTP connection
    */
  def close(): Unit
}
