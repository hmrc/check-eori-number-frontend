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

import play.api.data.Forms.{mapping, text}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Mapping}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.checkeorinumberfrontend.connectors.CheckEoriNumberConnector
import uk.gov.hmrc.checkeorinumberfrontend.controllers.actions.{RetrieveEoriAction, UnauthenticatedAction}
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.checkeorinumberfrontend.repositories.EoriNumberCache
import uk.gov.hmrc.checkeorinumberfrontend.views.html.templates._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckEoriNumberController @Inject() (
  mcc: MessagesControllerComponents,
  unauthenticatedAction: UnauthenticatedAction,
  retrieveEori: RetrieveEoriAction,
  connector: CheckEoriNumberConnector,
  eoriNumberCache: EoriNumberCache,
  checkPage: CheckPage,
  validEoriResponsePage: ValidEoriResponsePage,
  invalidEoriResponsePage: InvalidEoriResponsePage,
  xiEoriResponsePage: XIEoriResponsePage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  import CheckEoriNumberController.form

  def checkForm: Action[AnyContent] = Action { implicit request =>
    Ok(checkPage(form))
  }

  def onSubmit(): Action[AnyContent] = unauthenticatedAction.async { request =>
    given play.api.mvc.Request[AnyContent] = request
    form.bindFromRequest().fold(
      errors => Future.successful(BadRequest(checkPage(errors))),
      value =>
        eoriNumberCache.set(request.recordId, value) map { _ =>
          Redirect(routes.CheckEoriNumberController.result())
        }
    )
  }

  def result: Action[AnyContent] = (unauthenticatedAction andThen retrieveEori).async { implicit request =>
    for {
      checkResponsesOpt <- connector.check(request.eoriNumber)
    } yield {
      checkResponsesOpt match {
        case Some(head :: _) if head.eori.matches("XI[0-9]{12}|XI[0-9]{15}$") =>
          Ok(xiEoriResponsePage(head))
        case Some(head :: _) if head.valid                                    =>
          Ok(validEoriResponsePage(head))
        case Some(head :: _)                                                  =>
          Ok(invalidEoriResponsePage(head))
        case _                                                                =>
          throw new MissingCheckResponseException
      }
    }
  }
  class MissingCheckResponseException extends RuntimeException("no CheckResponse from CheckEoriNumberConnector")
}

object CheckEoriNumberController {

  private val eoriRegex: String = "^(GB|XI)[0-9]{12}|(GB|XI)[0-9]{15}$"

  val form: Form[CheckSingleEoriNumberRequest] = Form(
    mapping(
      "eori" -> mandatoryEoriNumber("eori")
    )(CheckSingleEoriNumberRequest.apply)(o => Some(o.eoriNumber))
  )

  private def combine[T](c1: Constraint[T], c2: Constraint[T]): Constraint[T] = Constraint { v =>
    c1.apply(v) match {
      case Valid      => c2.apply(v)
      case i: Invalid => i
    }
  }
  private def required(key: String): Constraint[String]                       = Constraint {
    case "" => Invalid(s"error.$key.required")
    case _  => Valid
  }

  private def eoriNumberConstraint(key: String): Constraint[String] = Constraint {
    case a if !a.matches(eoriRegex) => Invalid(s"error.$key.invalid")
    case _                          => Valid
  }

  private def mandatoryEoriNumber(key: String): Mapping[String] =
    text.transform[String](_.trim, s => s).verifying(combine(required(key), eoriNumberConstraint(key)))
}
