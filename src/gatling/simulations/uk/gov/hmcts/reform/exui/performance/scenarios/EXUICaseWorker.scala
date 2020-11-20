package uk.gov.hmcts.reform.exui.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils.{CaseworkerHeader, Environment}

object EXUICaseWorker {

  val IdamUrl = Environment.idamURL
  val baseURL = Environment.baseURL
  val caseFeeder = csv("CaseworkerSearches.csv").random

  val MinThinkTime = Environment.minThinkTimeCW
  val MaxThinkTime = Environment.maxThinkTimeCW

  val ApplyFilters =
    feed(caseFeeder)
      .exec(http("XUI${service}_030_ApplyFilter")
        .post("/data/internal/searchCases?ctid=${caseType}&use_case=SEARCH&view=SEARCH&page=1").headers(CaseworkerHeader.headers_2)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .body(StringBody("{\n  \"size\": 25\n}"))
        .check(jsonPath("$..case_id").findAll.optional.saveAs("caseNumbers")))
      .pause(MinThinkTime, MaxThinkTime)

  val ViewCase = doIf(session => session.contains("caseNumbers")) {
    foreach("${caseNumbers}", "caseNumber") {
      exec(http("XUI${service}_040_005_ViewCase")
        .get("/data/internal/cases/${caseNumber}")
        .headers(CaseworkerHeader.headers_5)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(regex("""internal/documents/(.+?)","document_filename""")
          .find(0).optional.saveAs("Document_ID")))

        .exec(http("XUI${service}_040_010_ViewUndefined")
          .get("/undefined/cases/${caseNumber}")
          .headers(CaseworkerHeader.headers_undefined))

        .exec(http("XUI${service}_040_015_GetPaymentGroups")
          .get("/payments/cases/${caseNumber}/paymentgroups")
          .headers(CaseworkerHeader.headers_search).check(status.in(200,404)))
        .pause(MinThinkTime, MaxThinkTime)

      //TO DO - put this in a do-if statement, so only do these steps if document_ID is found
      .doIf(session => session.contains("Document_ID")) {
        exec(http("XUI${service}_050_005_ViewCaseDocumentUI")
          .get("/external/config/ui")
          .headers(CaseworkerHeader.headers_documents)
          .header("X-XSRF-TOKEN", "${xsrfToken}"))

          .exec(http("XUI${service}_050_010_ViewCaseDocumentT&C")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(CaseworkerHeader.headers_documents)
            .header("X-XSRF-TOKEN", "${xsrfToken}"))

          .exec(http("XUI${service}_050_015_ViewCaseDocumentAnnotations")
            .get("/em-anno/annotation-sets/filter?documentId=${Document_ID}")
            .headers(CaseworkerHeader.headers_documents)
            .header("X-XSRF-TOKEN", "${xsrfToken}")
            .check(status.in(200, 404, 304,502)))

          .exec(http("XUI${service}_050_020_ViewCaseDocumentBinary")
            .get("/documents/${Document_ID}/binary")
            .headers(CaseworkerHeader.headers_documents)
            .header("X-XSRF-TOKEN", "${xsrfToken}")
            .check(status.in(200, 404, 304)))
          .pause(MinThinkTime, MaxThinkTime)

        //Simulate clicking on Case List
      }

    }
  }
}


