/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.checkeorinumberfrontend.controllers

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verifyNoInteractions, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.checkeorinumberfrontend.connectors.CheckEoriNumberConnector
import uk.gov.hmrc.checkeorinumberfrontend.controllers.actions.UnauthenticatedAction
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.checkeorinumberfrontend.models.{CheckResponse, EoriNumber}
import uk.gov.hmrc.checkeorinumberfrontend.repositories.EoriNumberCache
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec
import uk.gov.hmrc.checkeorinumberfrontend.views.html.templates.{CheckPage, InvalidEoriResponsePage, ValidEoriResponsePage, XIEoriResponsePage}

import scala.concurrent.Future

class CheckEoriNumberControllerSpec extends BaseSpec with BeforeAndAfterEach {

  val checkPage: CheckPage                                   = app.injector.instanceOf[CheckPage]
  val validEoriResponsePage: ValidEoriResponsePage           = app.injector.instanceOf[ValidEoriResponsePage]
  val invalidEoriResponsePage: InvalidEoriResponsePage       = app.injector.instanceOf[InvalidEoriResponsePage]
  val xiEoriResponsePage: XIEoriResponsePage                 = app.injector.instanceOf[XIEoriResponsePage]
  val eoriNumber: EoriNumber                                 = "GB123456787665"
  val xiEoriNumber: EoriNumber                               = "XI123456789123456"
  val mockCheckEoriNumberConnector: CheckEoriNumberConnector = mock[CheckEoriNumberConnector]
  val mockEoriNumberCache: EoriNumberCache                   = mock[EoriNumberCache]
  val mockValidResponse: List[CheckResponse]                 = List(CheckResponse(eoriNumber, valid = true, None))

  val controller = new CheckEoriNumberController(
    mcc,
    app.injector.instanceOf[UnauthenticatedAction],
    mockCheckEoriNumberConnector,
    mockEoriNumberCache,
    checkPage,
    validEoriResponsePage,
    invalidEoriResponsePage,
    xiEoriResponsePage
  )

  override def beforeEach(): Unit = {
    reset(mockCheckEoriNumberConnector)
    reset(mockEoriNumberCache)
    super.beforeEach()
  }

  "GET /" should {
    "return 200 and load the page" in {
      val result = controller.checkForm(fakeRequest)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include(messagesApi("checkpage.title"))
    }

    "return HTML" in {
      val result = controller.checkForm(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "POST /" should {

    val request = FakeRequest("POST", "/")

    "return 303, save to cache and redirect to /result when valid data is submitted" in {

      when(mockEoriNumberCache.set(any(), eqTo(CheckSingleEoriNumberRequest(eoriNumber))))
        .thenReturn(Future.successful(true))

      val result = controller.onSubmit()(request.withFormUrlEncodedBody("eori" -> eoriNumber))
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.CheckEoriNumberController.result().url)
    }

    "return 400 and render error page" when {

      "form is empty" in {
        val result = controller.onSubmit()(request.withFormUrlEncodedBody("eori" -> ""))
        contentAsString(result) should include(messagesApi("error.eori.required"))
        verifyNoInteractions(mockEoriNumberCache)
      }

      "form with invalid data" in {
        val result = controller.onSubmit()(request.withFormUrlEncodedBody("eori" -> "XYZ"))
        contentAsString(result) should include(messagesApi("error.eori.invalid"))
        verifyNoInteractions(mockEoriNumberCache)
      }
    }
  }

  "POST /result" should {

    val validRequest = FakeRequest("POST", "/results")

    val validXIRequest = FakeRequest("POST", "/results")
      .withFormUrlEncodedBody("eori" -> xiEoriNumber)

    "return 200" in {

      when(mockEoriNumberCache.get(any()))
        .thenReturn(Future.successful(Some(CheckSingleEoriNumberRequest(eoriNumber))))

      when(mockCheckEoriNumberConnector.check(any())(any(), any()))
        .thenReturn(Future.successful(Some(mockValidResponse)))

      val result = controller.result(validRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) should include(messagesApi("result.valid.heading"))
      contentAsString(result) should include(messagesApi("common.feedback.title"))
      contentAsString(result) should include(messagesApi("common.feedback.p1"))
      contentAsString(result) should include(messagesApi("common.feedback.link"))
      contentAsString(result) should include(messagesApi("common.feedback.p2"))
    }

    "contain content for an XI EORI" in {

      when(mockEoriNumberCache.get(any()))
        .thenReturn(Future.successful(Some(CheckSingleEoriNumberRequest(xiEoriNumber))))

      when(mockCheckEoriNumberConnector.check(any())(any(), any())).thenReturn(
        Future.successful(Some(mockValidResponse.map(_.copy(eori = xiEoriNumber))))
      )

      val result = controller.result(validXIRequest)
      contentAsString(result) should include(messagesApi("result.xi.heading"))
    }

    "contain content for invalid EORI" in {

      val invalidEori = "GB999999999999"

      val badRequest = FakeRequest("POST", "/results")
        .withFormUrlEncodedBody("eori" -> invalidEori)

      when(mockEoriNumberCache.get(any()))
        .thenReturn(Future.successful(Some(CheckSingleEoriNumberRequest(invalidEori))))

      when(mockCheckEoriNumberConnector.check(any())(any(), any())).thenReturn(
        Future.successful(Some(mockValidResponse.map(_.copy(valid = false))))
      )
      val result = controller.result(badRequest)
      contentAsString(result) should include(messagesApi("result.invalid.heading"))
      contentAsString(result) should include(messagesApi("common.feedback.title"))
      contentAsString(result) should include(messagesApi("common.feedback.p1"))
      contentAsString(result) should include(messagesApi("common.feedback.link"))
      contentAsString(result) should include(messagesApi("common.feedback.p2"))
    }

    "throw an exception" when {

      "EORI number cannot be retrieved from cache" in {

        when(mockEoriNumberCache.get(any()))
          .thenReturn(Future.successful(None))

        val result = intercept[controller.MissingCheckResponseException](await(controller.result(validRequest)))
        result.getMessage shouldBe "no CheckResponse from CheckEoriNumberConnector"

        verifyNoInteractions(mockCheckEoriNumberConnector)
      }

      "backend returns No data" in {

        when(mockEoriNumberCache.get(any()))
          .thenReturn(Future.successful(Some(CheckSingleEoriNumberRequest(eoriNumber))))

        when(mockCheckEoriNumberConnector.check(any())(any(), any()))
          .thenReturn(Future.successful(None))

        val result = intercept[controller.MissingCheckResponseException](await(controller.result(validRequest)))
        result.getMessage shouldBe "no CheckResponse from CheckEoriNumberConnector"
      }
    }
  }
}
