package uk.gov.hmcts.reform.exui.performance.scenarios

import java.text.SimpleDateFormat
import java.util.Date
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.IACHeader._

import scala.util.Random

object EXUIIACMC {

  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL

  val MinThinkTime = Environment.minThinkTimeIACC
  val MaxThinkTime = Environment.maxThinkTimeIACC
  val MinThinkTimeIACV = Environment.minThinkTimeIACV
  val MaxThinkTimeIACV = Environment.maxThinkTimeIACV

  private val rng: Random = new Random()
  private def firstName(): String = rng.alphanumeric.take(10).mkString
  private def lastName(): String = rng.alphanumeric.take(10).mkString

  val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
  val now = new Date()
  val timeStamp = sdfDate.format(now)

  val iaccasecreation=
    tryMax(2) {

      //set the current date as a usable parameter
      exec(session => session.set("currentDate", timeStamp))

        //set the random variables as usable parameters
        .exec(
        _.setAll(
          ("firstName", firstName()),
          ("lastName", lastName())
        ))
        //when click on create
        .exec(http("XUI${service}_040_CreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(IACHeader.headers_createcase)
        .check(status.in(200, 304))).exitHereIfFailed
    }
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_050_005_StartCreateCase1")
        .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
        .headers(IACHeader.headers_startcreatecase)
        .check(status.is(200))
        .check(bodyString.saveAs("BODY7"))
        .check(jsonPath("$..event_token").optional.saveAs("event_token")))
      .exec{
        session =>
          println("This is the response body of StartCreateCase1:" + session("BODY7").as[String])
          session
      }

      .exec(http("XUI${service}_050_010_StartCreateCase2")
      .get("/data/internal/case-types/Asylum/event-triggers/startAppeal?ignore-warning=false")
      .headers(IACHeader.headers_startcreatecase)
      .check(status.is(200))
      //.check(jsonPath("$.event_token").optional.saveAs("event_token"))
    )

      .exec(http("XUI${service}_050_015_CaseCreateProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_data_internal)
        .check(status.in(200,304,302)))

      .pause(MinThinkTime , MaxThinkTime)


      .exec(http("XUI${service}_060_StartAppealChecklist")
        .post("/data/case-types/Asylum/validate?pageId=startAppealchecklist")
        .headers(IACHeader.headers_9)
        .body(StringBody("""{"data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]}},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]}},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.is(200))).exitHereIfFailed

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_070_StartAppealHomeOfficeDecision")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhomeOfficeDecision")
        .headers(IACHeader.headers_homeofficedecision)
        .body(StringBody("""{"data":{"homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      //below is newly added transaction

      .exec(http("XUI${service}_080_005_Documents")
        .post("/documents")
        .headers(IACHeader.headers_document)
        .bodyPart(RawFileBodyPart("files", "3MB.pdf")
          .fileName("3MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .check(regex("""http://(.+)/""").saveAs("DMURL"))
        .check(regex("""internal/documents/(.+?)/binary""").saveAs("Document_ID"))
        .check(status is (200)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_080_010_StartUploadNoticeDecision")
        .post("/data/case-types/Asylum/validate?pageId=startAppealuploadTheNoticeOfDecision")
        .headers(IACHeader.headers_uploadnotice)
        .body(StringBody("""{"data":{"uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}]},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}]},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

    .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_090_StartAppealBasicDetails")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantBasicDetails")
        .headers(IACHeader.headers_basicdetails)
        .body(StringBody("""{"data":{"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      //below is the new request
      .exec(http("XUI${service}_100_StartAppealantNationality")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantNationalities")
        .headers(IACHeader.headers_nationality)
        .body(StringBody("""{"data":{"appellantStateless":"hasNationality","appellantNationalities":[{"value":{"code":"GB"},"id":null}]},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantNationalities":[{"value":{"code":"GB"},"id":null}]},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

    .pause(MinThinkTime , MaxThinkTime )


      .exec(http("XUI${service}_110_StartAppealDetailsAddressSearch")
        .get("/api/addresses?postcode=TW33SD")
        .headers(IACHeader.headers_postcode))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_120_StartAppealAppellantAddress")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantAddress")
        .headers(IACHeader.headers_appelantaddress)
        .body(StringBody("""{"data":{"appellantHasFixedAddress":"Yes","appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"}},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"}},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_130_AppellantContactPref")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappellantContactPreference")
        .headers(IACHeader.headers_contactpref)
        .body(StringBody("""{"data":{"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_140_StartAppealAppealType")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealType")
        .headers(IACHeader.headers_appealtype)
        .body(StringBody("""{"data":{"appealType":"refusalOfEu"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_150_StartAppealGroundsRevocation")
        .post("/data/case-types/Asylum/validate?pageId=startAppealappealGroundsEuRefusal")
        .headers(IACHeader.headers_eurefusal)
        .body(StringBody("""{"data":{"appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]}},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]}},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_160_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealdeportationOrderPage")
        .headers(IACHeader.headers_orderpage)
        .body(StringBody("""{"data":{"deportationOrderOptions":"No"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]},"deportationOrderOptions":"No"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_170_StartAppealNewMatters")
        .post("/data/case-types/Asylum/validate?pageId=startAppealnewMatters")
        .headers(IACHeader.headers_newmatters)
        .body(StringBody("""{"data":{"hasNewMatters":"No"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]},"deportationOrderOptions":"No","hasNewMatters":"No"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_180_StartAppealHasOtherAppeals")
        .post("/data/case-types/Asylum/validate?pageId=startAppealhasOtherAppeals")
        .headers(IACHeader.headers_otherappeals)
        .body(StringBody("""{"data":{"hasOtherAppeals":"No"},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]},"deportationOrderOptions":"No","hasNewMatters":"No","hasOtherAppeals":"No"},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_190_StartAppealLegalRepresentative")
        .post("/data/case-types/Asylum/validate?pageId=startAppeallegalRepresentativeDetails")
        .headers(IACHeader.headers_repdetails)
        .body(StringBody("""{"data":{"legalRepCompany":"Solirius","legalRepName":"John Smith","legalRepReferenceNumber":"abcd","isFeePaymentEnabled":null,"isRemissionsEnabled":null},"event":{"id":"startAppeal","summary":"","description":""},"event_data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]},"deportationOrderOptions":"No","hasNewMatters":"No","hasOtherAppeals":"No","legalRepCompany":"Solirius","legalRepName":"John Smith","legalRepReferenceNumber":"abcd","isFeePaymentEnabled":null,"isRemissionsEnabled":null},"event_token":"${event_token}","ignore_warning":false}"""))
        .check(status.in(200, 304)))

      .exec(http("XUI${service}_200_RepresentativeProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_repprofile)
        .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_210_StartAppealCaseSave")
        .post("/data/case-types/Asylum/cases?ignore-warning=false")
        .headers(IACHeader.headers_casesave)
        .body(StringBody("""{"data":{"isOutOfCountryEnabled":"Yes","checklist":{"checklist2":["isNotDetained"],"checklist7":["isNotEUDecision"]},"appellantInUk":"Yes","homeOfficeReferenceNumber":"123456789","homeOfficeDecisionDate":"2021-05-24","uploadTheNoticeOfDecisionDocs":[{"value":{"description":"This is the document","document":{"document_url":"http://${DMURL}/${Document_ID}","document_binary_url":"http://${DMURL}/${Document_ID}/binary","document_filename":"3MB.pdf"}},"id":null}],"appellantTitle":"Mr","appellantGivenNames":"John","appellantFamilyName":"Smith","appellantDateOfBirth":"1990-05-12","appellantStateless":"hasNationality","appellantHasFixedAddress":"Yes","appellantNationalities":[{"value":{"code":"AE"},"id":null}],"appellantAddress":{"AddressLine1":"Ministry Of Justice","AddressLine2":"Seventh Floor 102 Petty France","AddressLine3":"","PostTown":"London","County":"","PostCode":"SW1H 9AJ","Country":"United Kingdom"},"contactPreference":"wantsEmail","email":"iacaatorg-ye32a-user1@mailtest.gov.uk","appealType":"refusalOfEu","appealGroundsEuRefusal":{"values":["appealGroundsEuRefusal"]},"deportationOrderOptions":"No","hasNewMatters":"No","hasOtherAppeals":"No","legalRepCompany":"Solirius","legalRepName":"John Smith","legalRepReferenceNumber":"abcd","isFeePaymentEnabled":null,"isRemissionsEnabled":null},"event":{"id":"startAppeal","summary":"","description":""},"event_token":"${event_token}","ignore_warning":false,"draft_id":null}"""))
        .check(status.in(201, 304))
        .check(jsonPath("$.id").optional.saveAs("caseId")))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_220_005_StartSubmitAppeal")
        .get("/case/IA/Asylum/${caseId}/trigger/submitAppeal")
        .headers(IACHeader.headers_submitappeal)
        .check(status.in(200, 304))
      )

      .exec(http("XUI${service}_220_010_StartSubmitAppealUI")
        .get("/external/config/ui")
        .headers(IACHeader.headers_configui)
        .check(status.in(200, 304))
      )
      .exec(http("XUI${service}_220_015_SubmitAppealTCEnabled1")
        .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(IACHeader.headers_configui)
        .check(status.in(200, 304))
      )
      .exec(http("XUI${service}_220_020_IsAuthenticated")
        .get("/auth/isAuthenticated")
        .headers(IACHeader.headers_configui)
        .check(status.in(200, 304))
      )

      .exec(http("XUI${service}_220_025_SaveCaseView")
        .get("/data/internal/cases/${caseId}")
        .headers(IACHeader.headers_caseview)
        .check(status.in(200,304,302)))

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_230_005_SubmitAppeal")
        .get("/data/internal/cases/${caseId}/event-triggers/submitAppeal?ignore-warning=false")
        .headers(IACHeader.headers_newsubmitappeal)
        .check(status.in(200, 304))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_submit")))

      .exec(http("XUI${service}_230_010_IsAuthenticated")
        .get("/auth/isAuthenticated")
        .headers(IACHeader.headers_isauthenticatedsubmit)
        .check(status.in(200,304,302)))

      .exec(http("XUI${service}_230_015_UserDetails")
            .get("/api/user/details")
            .headers(IACHeader.headers_isauthenticatedsubmit)
            .check(status.in(200,304,302)))

      .exec(http("XUI${service}_230_020_DataInternalProfile")
            .get("/data/internal/profile")
            .headers(IACHeader.headers_internalprofiledatasubmit)
            .check(status.in(200,304,302)))
      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_240_005_SubmitAppealDeclaration")
        .post("/data/case-types/Asylum/validate?pageId=submitAppealdeclaration")
        .headers(IACHeader.headers_submitdeclaration)
        .body(StringBody("""{"data":{"legalRepDeclaration":["hasDeclared"]},"event":{"id":"submitAppeal","summary":"","description":""},"event_data":{"legalRepDeclaration":["hasDeclared"]},"event_token":"${event_token_submit}","ignore_warning":false,"case_reference":"${caseId}"}"""))
        .check(status.in(200, 304))
      )

      .exec(http("XUI${service}_240_010_SubmitAppealProfile")
        .get("/data/internal/profile")
        .headers(IACHeader.headers_internaldeclaration)
        .check(status.in(200, 304))
      )

      .pause(MinThinkTime , MaxThinkTime )

      .exec(http("XUI${service}_250_AppealDeclarationSubmitted")
        .post("/data/cases/${caseId}/events")
        .headers(IACHeader.headers_declarationsubmitted)
        .body(StringBody("""{"data":{"legalRepDeclaration":["hasDeclared"]},"event":{"id":"submitAppeal","summary":"","description":""},"event_token":"${event_token_submit}","ignore_warning":false}"""))
        .check(status.in(201, 304))
      )
      .pause(MinThinkTime , MaxThinkTime )


  val sharecase =
    exec(http("XUI${service}_260_005_Cases")
      .get("/cases")
      .headers(sharecase_headers_9)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_010_UserDetails")
      .get("/api/user/details")
      .headers(sharecase_headers_28)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_015_TermsAndConditions")
      .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
      .headers(sharecase_headers_28)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_020_Organisation")
      .get("/api/organisation")
      .headers(sharecase_headers_28)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_025_AggregatedCaseworkers")
      .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
      .headers(sharecase_headers_50)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_030_WorkBasketInputs1")
      .get("/data/internal/case-types/BUND_ASYNC_-1014887449/work-basket-inputs")
      .headers(sharecase_headers_52)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_035_SearchCases1")
      .post("/data/internal/searchCases?ctid=BUND_ASYNC_-1014887449&use_case=WORKBASKET&view=WORKBASKET&page=1")
      .headers(sharecase_headers_57)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_040_WorkBasketInputs2")
      .get("/data/internal/case-types/Asylum-XUI/work-basket-inputs")
      .headers(sharecase_headers_52)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_045_WorkBasketInputs3")
      .get("/data/internal/case-types/Asylum/work-basket-inputs")
      .headers(sharecase_headers_52)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_050_SearchCases2")
      .post("/data/internal/searchCases?ctid=Asylum&use_case=WORKBASKET&view=WORKBASKET&state=appealSubmitted&page=1")
      .headers(sharecase_headers_115)
      .body(StringBody("""{"size":25}"""))
      .check(status.in(200, 304)))

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI${service}_270_005_CaseShare1")
      .get("/api/caseshare/cases?case_ids=${caseId}")
      .headers(sharecase_headers_28)
      .check(status.in(200, 304)))

    .exec(http("XUI${service}_260_025_CaseShare2")
      .get("/api/caseshare/users")
      .headers(sharecase_headers_28)
      .check(status.in(200, 304)))

    .pause(MinThinkTime, MaxThinkTime)

    .exec(http("XUI${service}_260_025_CaseShare3")
      .post("/api/caseshare/case-assignments")
      .headers(sharecase_headers_227)
      .body(StringBody("""{"sharedCases":[{"caseId":"${caseId}","caseTitle":"${caseId}","caseTypeId":"Asylum","sharedWith":[{"caseRoles":["[LEGALREPRESENTATIVE]"],"email":"iacaatorg-yw8gz-user0@mailinator.com","firstName":"VUser","idamId":"ec08ccab-453f-4fa8-82cc-eb6507c4fa6f","lastName":"VykUser"}],"pendingShares":[{"email":"iacaatorg-yw8gz-user2@mailinator.com","firstName":"VUser","idamId":"2f463abe-f787-47c7-b14a-45f44bb4a6ce","lastName":"VykUser"}],"pendingUnshares":[]}]}"""))
      .check(status.in(201, 304)))


  val findandviewcase =

    exec(http("XUI${service}_040_005_SearchPage")
         .get("/aggregated/caseworkers/:uid/jurisdictions?access=read")
         .headers(IACHeader.headers_search)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
    )

      .exec(http("XUI${service}_040_010_SearchPage")
			.get("/data/internal/case-types/Asylum/search-inputs")
			.headers(IACHeader.headers_searchinputs)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
      )


    .exec(http("XUI${service}_040_015_SearchPaginationMetaData")
			.get("/aggregated/caseworkers/:uid/jurisdictions/IA/case-types/Asylum/cases?view=SEARCH&page=1&case.searchPostcode=TW3%203SD")
			.headers(IACHeader.headers_search)
      .header("X-XSRF-TOKEN", "${XSRFToken}"))

      .pause(MinThinkTimeIACV,MaxThinkTimeIACV)

    .exec(http("XUI${service}_040_020_SearchResults")
			.post("/data/internal/searchCases?ctid=Asylum&use_case=SEARCH&view=SEARCH&page=1&case.searchPostcode=TW3%203SD")
			.headers(IACHeader.headers_searchresults)
      .header("X-XSRF-TOKEN", "${XSRFToken}")
      .body(StringBody("{\n  \"size\": 25\n}"))
     // .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))
          .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))

    /*  .exec(http("XUI${service}_040_025_SearchPage")
            .get("/data/internal/case-types/Asylum/search-inputs")
            .headers(IACHeader.headers_searchinputs)
            .header("X-XSRF-TOKEN", "${XSRFToken}")
      )*/

        .pause(MinThinkTimeIACV,MaxThinkTimeIACV)

        //.foreach("${caseNumbers}","caseNumber") {
              .exec(http("XUI${service}_050_CaseDetails")
            .get("/data/internal/cases/${caseNumber}")
            .headers(IACHeader.headers_data_internal_cases)
            .header("X-XSRF-TOKEN", "${XSRFToken}")
            .check(regex("""internal/documents/(.+?)","document_filename""").find(0).optional.saveAs("Document_ID"))
            .check(status.is(200)))

            .pause(MinThinkTimeIACV,MaxThinkTimeIACV)

        .exec(http("XUI${service}_060_005_ViewCaseDocumentUI")
          .get("/external/config/ui")
          .headers(IACHeader.headers_documents)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
        )

        .exec(http("XUI${service}_060_010_ViewCaseDocumentT&C")
          .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
          .headers(IACHeader.headers_documents)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
        )
      .doIf(session => session.contains("Document_ID")) {
      exec(http("XUI${service}_060_015_ViewCaseDocumentAnnotations")
        .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
        .headers(IACHeader.headers_documents)
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .check(status.in(200, 404, 304)))
        .exec(http("XUI${service}_060_020_ViewCaseDocumentBinary")
          .get("/documents/${Document_ID}/binary")
          .headers(IACHeader.headers_documents)
          .header("X-XSRF-TOKEN", "${XSRFToken}")
          .check(status.in(200, 404, 304)))
        .pause(MinThinkTimeIACV, MaxThinkTimeIACV)

    }
    //  }

}