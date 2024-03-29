package uk.gov.hmcts.reform.exui.performance.scenarios.utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

 val environment: String = System.getProperty("env")
 //if (environment == "aat") {
 val S2S_ServiceName = "rd_professional_api"
  val S2SUrl = "http://rpe-service-auth-provider-aat.service.core-compute-aat.internal/testing-support"
  val PRDUrl = "http://rd-professional-api-aat.service.core-compute-aat.internal"
  val url_approve = "https://administer-orgs.aat.platform.hmcts.net"
  val manageOrdURL = "https://manage-org.aat.platform.hmcts.net"
  val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val baseURL = "https://manage-case.aat.platform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-aat.service.core-compute-aat.internal"
  val baseFPLAURL = "https://manage-case.aat.platform.hmcts.net"
 val baseDomain="manage-case.aat.platform.hmcts.net"
val baseDomainOrg="manage-org.aat.platform.hmcts.net"
  val idamAPI="https://idam-api.aat.platform.hmcts.net"
  val notificationClient="sidam_aat-b7ab8862-25b4-41c9-8311-cb78815f7d2d-ebb113ff-da17-4646-a39e-f93783a993f4"
  val minThinkTime = //100
  5
  val maxThinkTime = //110
  15
  val minThinkTimeFPLC = 75
  //10
  val maxThinkTimeFPLC = 80

  val minThinkTimeSDO = 100
  //10
  val maxThinkTimeSDO = 110
  //30
  val minThinkTimeIACC = //100
  5
  val maxThinkTimeIACC = //120
  15
  val minThinkTimeFPLV = 450
  //10
  val maxThinkTimeFPLV = 480
  //320
  val minThinkTimeIACV = //470
  5
  val maxThinkTimeIACV = //490
  15
  val minThinkTimePROB = 300
  val maxThinkTimePROB = 310

  val minThinkTimeDIV = 140
  val maxThinkTimeDIV = 160

  val minThinkTimeCW = //50
  5
  val maxThinkTimeCW = //50
  15

  val minThinkTimeFR =10//  100
  val maxThinkTimeFR =10//100

  val constantthinkTime = 5
  val HttpProtocol = http

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")

}
