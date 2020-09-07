/*
 * Copyright 2020 HM Revenue & Customs
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

import java.net.URLEncoder.encode

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  val footerLinkItems: Seq[String] = config.getOptional[Seq[String]]("footerLinkItems").getOrElse(Seq())

  private val contactHost = servicesConfig.getConfString(s"contact-frontend.host", "")
  private val encoding = "UTF-8"
  private val contactFormServiceIdentifier = config.get[String]("appName")

  lazy val feedbackSurveyUrl: String = servicesConfig.getConfString("feedback-survey.url", "")
  lazy val chenUrl: String = servicesConfig.getConfString("check-eori-number.url", "")


  def contactAccessibilityHelpDeskLink(path: String): String = {
    s"$contactHost/contact/accessibility?service=$contactFormServiceIdentifier&userAction=${encode(path, encoding)}"
  }

}
