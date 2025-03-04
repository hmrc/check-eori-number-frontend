/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.checkeorinumberfrontend.controllers.actions

import com.google.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

case class RequestWithId[A](recordId: String, request: Request[A]) extends WrappedRequest[A](request)

case class RequestWithEori[A](eoriNumber: CheckSingleEoriNumberRequest, request: Request[A])
    extends WrappedRequest[A](request)

class UnauthenticatedAction @Inject() (
  mcc: MessagesControllerComponents
) extends ActionBuilder[RequestWithId, AnyContent]
    with ActionFunction[Request, RequestWithId] {

  implicit override val executionContext: ExecutionContext = mcc.executionContext
  override val parser: BodyParser[AnyContent]              = mcc.parsers.defaultBodyParser

  override def invokeBlock[A](
    request: Request[A],
    block: RequestWithId[A] => Future[Result]
  ): Future[Result] =
    HeaderCarrierConverter.fromRequestAndSession(request, request.session).sessionId match {
      case Some(id) => block(RequestWithId(id.value, request))
      case None     => throw new IllegalStateException("No Session ID found")
    }
}
