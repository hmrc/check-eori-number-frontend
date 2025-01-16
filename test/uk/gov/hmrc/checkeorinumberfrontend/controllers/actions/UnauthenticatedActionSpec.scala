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

package uk.gov.hmrc.checkeorinumberfrontend.controllers.actions

import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.AnyContent
import play.api.mvc.Results.{Ok, Redirect}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec

import scala.concurrent.Future

class UnauthenticatedActionSpec extends BaseSpec with ScalaFutures {

  "UnauthenticatedAction" must {

    val action = new UnauthenticatedAction(stubMessagesControllerComponents())

    "transform the request and execute block" when {

      "the session ID is found from the request session" in {

        val sessionId = "1234567890"
        val url       = "/test/success"
        val request   = FakeRequest().withSession("sessionId" -> sessionId)
        val result    = action.invokeBlock(request, (_: RequestWithId[AnyContent]) => Future.successful(Redirect(url)))

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(url)
      }
    }

    "throw" when {

      "no session ID can be found" in {

        assertThrows[IllegalStateException] {
          action.invokeBlock(
            FakeRequest(),
            (_: RequestWithId[AnyContent]) => Future.successful(Ok("Success!"))
          ).futureValue
        }
      }
    }
  }
}
