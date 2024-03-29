package uk.gov.hmcts.reform.exui.performance.scenarios.utils

object LoginHeader {
 val baseURL = Environment.baseURL
 val IdamUrl = Environment.idamURL

 val commonHeader = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Upgrade-Insecure-Requests" -> "1")


 val headers_tc = Map(
  "Content-Type" -> "application/json",
  "Origin" -> "https://manage-case.aat.platform.hmcts.net",
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin")

 val headers_tc_get = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "Sec-Fetch-Dest" -> "document",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "same-origin",
  "Sec-Fetch-User" -> "?1",
  "Upgrade-Insecure-Requests" -> "1")

 val headers_0 = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin")


 val headers_signout = Map(
  "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-mode" -> "navigate",
  "sec-fetch-site" -> "same-origin",
  "sec-fetch-user" -> "?1",
  "upgrade-insecure-requests" -> "1")


 val headers_login = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "cross-site",
  "Sec-Fetch-User" -> "?1",
  "Upgrade-Insecure-Requests" -> "1")

 val headers_login_submit = Map(
  "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Origin" -> IdamUrl,
  "Sec-Fetch-Mode" -> "navigate",
  "Sec-Fetch-Site" -> "same-origin",
  "Sec-Fetch-User" -> "?1",
  "Upgrade-Insecure-Requests" -> "1")


 val headers_hometc = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$38717637_792h4vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

 val headers_38 = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$38732415_350h4vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

 val headers_access_read = Map(
  "accept" -> "application/json",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "content-type" -> "application/json",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-dtpc" -> "3$38734236_77h15vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0",
  "x-dtreferer" ->  ({baseURL+"/accept-terms-and-conditions"}))

 val headers_17 = Map(
  "Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8",
  "Content-Type" -> "application/json",
  "Sec-Fetch-Dest" -> "empty",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin",
  "experimental" -> "true")

}
