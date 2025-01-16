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

import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.checkeorinumberfrontend.controllers.routes
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.checkeorinumberfrontend.repositories.EoriNumberCache
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec

import scala.concurrent.Future

class RetrieveEoriActionSpec extends BaseSpec with ScalaFutures with BeforeAndAfterEach {

  val mockEoriNumberCache: EoriNumberCache = mock[EoriNumberCache]

  val action = new RetrieveEoriAction(mockEoriNumberCache, stubMessagesControllerComponents())

  override def beforeEach(): Unit = {
    reset(mockEoriNumberCache)
    super.beforeEach()
  }

  "RetrieveEoriAction" must {

    "retrieve a EORI number from the cache using the ID from the given request" in {

      val id = "1234567890"
      val eori = "0987654321"
      val content = "/test/success"
      when(mockEoriNumberCache.get(eqTo(id)))
        .thenReturn(Future.successful(Some(CheckSingleEoriNumberRequest(eori))))

      val result = action.invokeBlock(RequestWithId(id, FakeRequest()), (_: RequestWithEori[_]) =>
        Future.successful(Ok(content)))

      status(result) shouldBe OK
      contentAsString(result) should include(content)

      verify(mockEoriNumberCache).get(eqTo(id))
    }

    "Redirect the user to the start page when no EORI number is in the cache" in {

      val id = "1234567890"
      when(mockEoriNumberCache.get(eqTo(id)))
        .thenReturn(Future.successful(None))

      val result = action.invokeBlock(RequestWithId(id, FakeRequest()), (_: RequestWithEori[_]) =>
        Future.successful(Ok("Success!"))
      )

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.CheckEoriNumberController.checkForm().url)

      verify(mockEoriNumberCache).get(eqTo(id))
    }
  }
}
