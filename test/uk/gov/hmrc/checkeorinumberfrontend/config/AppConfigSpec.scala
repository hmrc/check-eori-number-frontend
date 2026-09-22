/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.checkeorinumberfrontend.config

import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.{Lang, Messages, MessagesImpl}
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends BaseSpec with BeforeAndAfterEach{
  private val mockServiceConfig = mock[ServicesConfig]

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockServiceConfig)
  }

  "AppConfig" should {

    "have sessionCacheTtl defined" in {
      appConfig.sessionCacheTtl shouldBe 900
    }

    "have userResearchBannerEnabled defined" in {
      appConfig.userResearchBannerEnabled shouldBe false
    }

    "have eisUrl defined" in {
      appConfig.eisUrl shouldBe "http://localhost:8351/check-eori-number"
    }

    "have feedbackSurveyUrl defined" in {
      appConfig.feedbackSurveyUrl shouldBe "http://localhost:9514/feedback/check-eori-number"
    }

    "have userResearchBannerUrl defined for English" in {
      val msg: Messages = MessagesImpl(Lang("en"), messagesApi).messages
      appConfig.userResearchBannerUrl()(msg) shouldBe "https://banner-en"
    }

    "have userResearchBannerUrl defined for Welsh" in {
      val msg: Messages = MessagesImpl(Lang("cy"), messagesApi).messages
      appConfig.userResearchBannerUrl()(msg) shouldBe "https://banner-cy"
    }

    "have languageMap defined" in {
      appConfig.languageMap shouldBe Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))
    }

  }

}