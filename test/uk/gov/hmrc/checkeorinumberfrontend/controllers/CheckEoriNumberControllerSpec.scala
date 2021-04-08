/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.checkeorinumberfrontend.models.{EoriNumber, CheckResponse}
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.checkeorinumberfrontend.views.html.templates._
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec
import uk.gov.hmrc.checkeorinumberfrontend.connectors.CheckEoriNumberConnector
import uk.gov.hmrc.checkeorinumberfrontend.models.{EoriNumber, CheckResponse}
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{Future, ExecutionContext}

class CheckEoriNumberControllerSpec extends BaseSpec {

  val checkPage: CheckPage = app.injector.instanceOf[CheckPage]
  val validEoriResponsePage: ValidEoriResponsePage = app.injector.instanceOf[ValidEoriResponsePage]
  val invalidEoriResponsePage: InvalidEoriResponsePage = app.injector.instanceOf[InvalidEoriResponsePage]
  val xiEoriResponsePage: XIEoriResponsePage = app.injector.instanceOf[XIEoriResponsePage]
  val eoriNumber: EoriNumber = "GB123456787665"
  val xiEoriNumber: EoriNumber = "XI123456789123456"

  val controller = new CheckEoriNumberController(
    mcc,
    new MockCheckEoriNumberConnector,
    checkPage,
    validEoriResponsePage,
    invalidEoriResponsePage,
    xiEoriResponsePage
  )

  "GET /" should {
    "return 200" in {
      val result = controller.checkForm(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.checkForm(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "contain service content" in {
      val result = controller.checkForm(fakeRequest)
      contentAsString(result) should include(messagesApi("checkpage.title"))
    }
  }

  "POST /result" should {
    val validRequest = FakeRequest("POST", "/results")
      .withFormUrlEncodedBody("eori" -> eoriNumber)

    val validXIRequest = FakeRequest("POST", "/results")
      .withFormUrlEncodedBody("eori" -> xiEoriNumber)

    val badRequest = FakeRequest("POST", "/results")
      .withFormUrlEncodedBody("eori" -> "GB999999999999")

    "return 200" in {
      val result = controller.result(validRequest)
      status(result) shouldBe Status.OK
    }

    "contain content for valid EORI" in {
      val result = controller.result(validRequest)
      contentAsString(result) should include(messagesApi("result.valid.heading"))
    }

    "contain content for an XI EORI" in {
      val result = controller.result(validXIRequest)
      contentAsString(result) should include(messagesApi("result.xi.heading"))
    }

    "contain content for invalid EORI" in {
      val result = controller.result(badRequest)
      contentAsString(result) should include(messagesApi("result.invalid.heading"))
    }


  }

  class MockCheckEoriNumberConnector extends CheckEoriNumberConnector {

    val mockValidResponse = List(CheckResponse(eoriNumber, true, None))

    def check(
      check: CheckSingleEoriNumberRequest
    )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[List[CheckResponse]]] = check.eoriNumber match {
      case `eoriNumber` => Future.successful(Some(mockValidResponse))
      case `xiEoriNumber` => Future.successful(Some(mockValidResponse.map(_.copy(eori = xiEoriNumber))))
      case _ => Future.successful(Some(mockValidResponse.map(_.copy(valid = false))))
    }
  }

}