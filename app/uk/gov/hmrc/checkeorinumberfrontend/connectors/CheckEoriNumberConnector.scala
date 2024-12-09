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

package uk.gov.hmrc.checkeorinumberfrontend.connectors

import com.google.inject.ImplementedBy
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.checkeorinumberfrontend.config.AppConfig
import uk.gov.hmrc.checkeorinumberfrontend.models._
import uk.gov.hmrc.checkeorinumberfrontend.models.internal.CheckSingleEoriNumberRequest
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CheckEoriNumberConnectorImpl])
trait CheckEoriNumberConnector {
  def check(
    checkSingleEoriNumberRequest: CheckSingleEoriNumberRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[List[CheckResponse]]]
}

class CheckEoriNumberConnectorImpl @Inject() (
  http: HttpClientV2,
  appConfig: AppConfig
) extends CheckEoriNumberConnector {

  def check(
    checkSingleEoriNumberRequest: CheckSingleEoriNumberRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[List[CheckResponse]]] = {
    http.get(url"${appConfig.eisUrl}/check-eori/${checkSingleEoriNumberRequest.eoriNumber}")
      .execute[List[CheckResponse]].map(Some(_))
      .recoverWith {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND =>
          Future.successful(
            Some(
              List(CheckResponse(checkSingleEoriNumberRequest.eoriNumber, valid = false, None))
            )
          )
      }
  }
}
