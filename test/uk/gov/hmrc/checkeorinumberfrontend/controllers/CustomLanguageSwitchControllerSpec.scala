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

package uk.gov.hmrc.checkeorinumberfrontend.controllers;

import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Lang
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec
import uk.gov.hmrc.play.language.LanguageUtils

class CustomLanguageSwitchControllerSpec extends BaseSpec {
  val mockLanguageUtils: LanguageUtils = mock[LanguageUtils]

  val controller = new CustomLanguageSwitchController(
    appConfig,
    mockLanguageUtils,
    mcc
  )

  "CustomLanguageSwitchController" should {
    "return correct 'fallbackURL'" in {
      controller.fallbackURL shouldBe "/check-eori-number"
    }

    "return correct 'languageMap'" in {
      controller.languageMap shouldBe Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))
    }
  }

}
