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
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.checkeorinumberfrontend.controllers.routes

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  private val contactHost: String = servicesConfig.getConfString(s"contact-frontend.host", "")
  private val encoding: String = "UTF-8"

  lazy val serviceName: String = config.get[String]("serviceName")
  lazy val appAcronym: String = config.get[String]("appAcronym")

  lazy val chenUrl: String = servicesConfig.getConfString("check-eori-number.url", "")
  lazy val eisUrl: String = s"${servicesConfig.baseUrl("check-eori-number")}/${chenUrl}"

  lazy val feedbackSurveyUrl: String = servicesConfig.getConfString("feedback-survey.url", "")

  def contactAccessibilityHelpDeskLink(path: String): String = {
    s"$contactHost/contact/accessibility?service=$serviceName&userAction=${encode(path, encoding)}"
  }

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))

  def routeToSwitchLanguage: String => Call = (lang: String) => routes.CustomLanguageSwitchController.switchToLanguage(lang)

  lazy val languageTranslationEnabled: Boolean =
    config.getBoolean("microservice.services.features.welsh-translation").getOrElse(true)

}
