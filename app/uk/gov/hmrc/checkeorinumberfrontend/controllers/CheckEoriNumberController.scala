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

package uk.gov.hmrc.checkeorinumberfrontend.controllers

import javax.inject.Inject
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{mapping, text}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig
import uk.gov.hmrc.checkeorinumberfrontend.connectors.CheckEoriNumberConnector
import uk.gov.hmrc.checkeorinumberfrontend.models.{Check, CheckResponse}
import uk.gov.hmrc.checkeorinumberfrontend.views.html.templates.{CheckPage, InvalidEoriResponsePage, ValidEoriResponsePage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class CheckEoriNumberController @Inject()(
  mcc: MessagesControllerComponents,
  connector: CheckEoriNumberConnector,
  checkPage: CheckPage,
  validEoriResponsePage: ValidEoriResponsePage,
  invalidEoriResponsePage: InvalidEoriResponsePage
)(implicit config: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) {

  import CheckEoriNumberController.form

  def checkForm: Action[AnyContent] = Action.async { implicit request =>
     Future.successful(Ok(checkPage(form)))
  }

  def result: Action[AnyContent] = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errors => Future(BadRequest(checkPage(errors))),
      check => connector.check(check).flatMap {
        case Some(response: CheckResponse) if response.isValidEori =>
          Future.successful(Ok(validEoriResponsePage(response)))
        case Some(response: CheckResponse) =>
          Future.successful(Ok(invalidEoriResponsePage(response)))
      }
    )
  }

}

  object CheckEoriNumberController {

    private val eoriRegex: String = "^GB[0-9]{12,15}$"

    val form: Form[Check] = Form(
      mapping(
        "eori" -> mandatoryEoriNumber("eori")
      )(Check.apply)(Check.unapply)
    )

    private def combine[T](c1: Constraint[T], c2: Constraint[T]): Constraint[T] = Constraint { v =>
      c1.apply(v) match {
        case Valid => c2.apply(v)
        case i: Invalid => i
      }
    }
    
    private def required(key: String): Constraint[String] = Constraint {
      case "" => Invalid(s"error.$key.required")
      case _ => Valid
    }

    private def vatNumberConstraint(key: String): Constraint[String] = Constraint {
      case a if !a.matches(eoriRegex) => Invalid(s"error.$key.invalid")
      case _ => Valid
    }

    private def mandatoryEoriNumber(key: String): Mapping[String] = {
      text.transform[String](_.trim, s => s).verifying(combine(required(key),vatNumberConstraint(key)))
    }
  }
