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
import uk.gov.hmrc.auth.core._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

case class RequestWithId[A](uuid: String, request: Request[A]) extends WrappedRequest[A](request)

class UnauthenticatedAction @Inject() (
                                      mcc: MessagesControllerComponents
                                    ) extends ActionBuilder[RequestWithId, AnyContent] with ActionFunction[Request, RequestWithId] {

  implicit override val executionContext: ExecutionContext = mcc.executionContext
  override val parser: BodyParser[AnyContent]              = mcc.parsers.defaultBodyParser

  override def invokeBlock[A](
                               request: Request[A],
                               block: RequestWithId[A] => Future[Result]
                             ): Future[Result] = {
    request match {
      case requestWithId: RequestWithId[A] =>
        block(requestWithId)
      case request =>
        block(RequestWithId(UUID.randomUUID().toString, request))
    }
  }
}
