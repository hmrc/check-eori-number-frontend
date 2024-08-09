package uk.gov.hmrc.checkeorinumberfrontend.messages

import play.api.i18n.MessagesApi
import uk.gov.hmrc.checkeorinumberfrontend.utils.BaseSpec

class WelshMessageSpec extends BaseSpec {
  lazy val realMessagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  "all english messages" should {
    "have a welsh translation" in {
      val realMessages    = realMessagesApi.messages
      val englishMessages = realMessages("en")
      val welshMessages   = realMessages("cy")

      val missingWelshKeys = englishMessages.keySet.filterNot(welshMessages.keySet)

      if (missingWelshKeys.nonEmpty) {
        val failureText = missingWelshKeys.foldLeft(s"There are ${missingWelshKeys.size} missing Welsh translations:") {
          case (failureString, key) =>
            failureString + s"\n$key:${englishMessages(key)}"
        }

        fail(failureText)
      }
    }
  }

}
