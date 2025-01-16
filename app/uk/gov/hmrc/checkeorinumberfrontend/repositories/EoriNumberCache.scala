/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.checkeorinumberfrontend.repositories

import play.api.libs.json.Format
import uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.mongo.cache.{CacheIdType, EntityCache, MongoCacheRepository}
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EoriNumberCache @Inject() (
  appConfig: AppConfig,
  mongo: MongoComponent,
  timestampSupport: TimestampSupport
)(implicit ec: ExecutionContext)
    extends EntityCache[String, CheckSingleEoriNumberRequest] {

  lazy val format: Format[CheckSingleEoriNumberRequest] = CheckSingleEoriNumberRequest.format

  lazy val cacheRepo: MongoCacheRepository[String] = new MongoCacheRepository(
    mongoComponent = mongo,
    collectionName = "eori-number-cache",
    ttl = Duration(appConfig.sessionCacheTtl, TimeUnit.SECONDS),
    timestampSupport = timestampSupport,
    cacheIdType = CacheIdType.SimpleCacheId,
    replaceIndexes = false
  )

  def get(id: String): Future[Option[CheckSingleEoriNumberRequest]] =
    getFromCache(id)

  def set(id: String, eoriNumber: CheckSingleEoriNumberRequest): Future[Boolean] =
    putCache(id)(eoriNumber).map(_ => true)

  def remove(id: String): Future[Unit] =
    deleteFromCache(id)
}
