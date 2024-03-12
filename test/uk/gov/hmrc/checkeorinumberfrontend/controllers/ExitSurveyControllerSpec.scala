/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatest.OptionValues
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec

import scala.concurrent.Future

class ExitSurveyControllerSpec extends BaseSpec with OptionValues {
  val controller = new ExitSurveyController(
    appConfig,
    mcc
  )

  lazy val exitSurveyRoute: String = routes.ExitSurveyController.exitSurvey.url

  "ExitSurveyController" should {
    "redirect to correct 'exitSurvey' page" in {

      val result: Future[Result] = controller.exitSurvey(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should include("/feedback/check-eori-number")
    }
  }

}
