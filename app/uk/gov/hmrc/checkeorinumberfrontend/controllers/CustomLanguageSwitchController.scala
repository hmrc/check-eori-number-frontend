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

package uk.gov.hmrc.checkeorinumberfrontend.controllers

import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc._
import uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig
import uk.gov.hmrc.play.language._

import javax.inject.Inject

class CustomLanguageSwitchController @Inject() (
  val appConfig: AppConfig,
  languageUtils: LanguageUtils,
  controllerComponents: ControllerComponents
) extends LanguageController(languageUtils, controllerComponents)
    with I18nSupport {

  def fallbackURL: String = routes.CheckEoriNumberController.checkForm().url

  def languageMap: Map[String, Lang] = appConfig.languageMap
}
