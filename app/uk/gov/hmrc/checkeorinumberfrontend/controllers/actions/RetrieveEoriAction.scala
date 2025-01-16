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
import play.api.Logging
import play.api.mvc.{ActionRefiner, MessagesControllerComponents, Result, Results}
import uk.gov.hmrc.checkeorinumberfrontend.controllers.routes
import uk.gov.hmrc.checkeorinumberfrontend.repositories.EoriNumberCache

import scala.concurrent.{ExecutionContext, Future}

class RetrieveEoriAction @Inject() (
                                     eoriNumberCache: EoriNumberCache,
                                     mcc: MessagesControllerComponents
                                   ) extends ActionRefiner[RequestWithId, RequestWithEori]
  with Results
  with Logging {

  override protected def refine[A](request: RequestWithId[A]): Future[Either[Result, RequestWithEori[A]]] = {
    eoriNumberCache.get(request.recordId) map {
      case Some(value) => Right(RequestWithEori(value, request))
      case None        =>
        logger.warn("Could not retrieve record, redirecting to start of journey")
        Left(Redirect(routes.CheckEoriNumberController.checkForm()))
    }
  }

  override implicit protected def executionContext: ExecutionContext = mcc.executionContext
}
