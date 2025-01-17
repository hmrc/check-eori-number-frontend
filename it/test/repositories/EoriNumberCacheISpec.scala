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

package repositories

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Injecting
import uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.checkeorinumberfrontend.repositories.EoriNumberCache
import uk.gov.hmrc.mongo.TimestampSupport
import uk.gov.hmrc.mongo.test.CleanMongoCollectionSupport

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class EoriNumberCacheISpec
  extends AnyWordSpec
  with GuiceOneAppPerSuite
  with CleanMongoCollectionSupport
  with Matchers
  with ScalaFutures
  with Injecting {

  lazy val repository = new EoriNumberCache(inject[AppConfig], mongoComponent, inject[TimestampSupport])

  val id: String = UUID.randomUUID().toString

  val obj: CheckSingleEoriNumberRequest = CheckSingleEoriNumberRequest("AA1234567890")

  "EoriNumberCache" when {

    ".set is called" must {

      "return true when value is successfully set" in {

        repository.set(id, obj).futureValue mustBe true
      }
    }

    ".get is called" must {

      "return a EORI number when one is present with the set ID" in {

        repository.set(id, obj).futureValue

        repository.get(id).futureValue mustBe Some(obj)
      }

      "return None" when {

        "incorrect ID is given" in {

          repository.set(id, obj).futureValue

          repository.get("foo").futureValue mustBe None
        }

        "cache is empty" in {

          repository.get(id).futureValue mustBe None
        }
      }
    }

    ".remove is called" must {

      "delete a record with the given ID without deleting other records" in {

        val altId = "1234567890"
        val altObj = CheckSingleEoriNumberRequest("BB0987654321")

        repository.set(id, obj).futureValue
        repository.set(altId, altObj).futureValue

        repository.remove(id).futureValue

        repository.get(altId).futureValue mustBe Some(altObj)
        repository.get(id).futureValue mustBe None
      }
    }
  }
}
