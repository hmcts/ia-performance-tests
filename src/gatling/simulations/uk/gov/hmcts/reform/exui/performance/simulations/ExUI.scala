package uk.gov.hmcts.reform.exui.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.exui.performance.Feeders
import uk.gov.hmcts.reform.exui.performance.scenarios._
import uk.gov.hmcts.reform.exui.performance.scenarios.utils._

class ExUI extends Simulation {

	val BaseURL = Environment.baseURL
	val orgurl=Environment.manageOrdURL
	//val feedUserDataIACView = csv("IACDataView.csv").circular
	val feedUserDataIACCreate = csv("IACDataCreate.csv").circular
	val feedUserDataCaseworker = csv("Caseworkers.csv").circular

  val XUIHttpProtocol = Environment.HttpProtocol
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .baseUrl(orgurl)
    //.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")
    .headers(Environment.commonHeader)


  val IAChttpProtocol = Environment.HttpProtocol
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
		.baseUrl(BaseURL)
		//.baseUrl("https://xui-webapp-perftest.service.core-compute-perftest.internal")
		//.baseUrl("https://ccd-case-management-web-perftest.service.core-compute-perftest.internal")

   // .inferHtmlResources()
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")

	val EXUIScn = scenario("EXUI").repeat(1)
	 {
		exec(
		//S2SHelper.S2SAuthToken,
			//Create organisation
		/*ExUI.createSuperUser,
		ExUI.createOrg,
      ExUI.approveOrgHomePage,
		ExUI.approveOrganisationlogin,
			ExUI.approveOrganisationApprove,
			ExUI.approveOrganisationLogout,*/
			//Invite users
			ExUI.manageOrgHomePage,
			ExUI.manageOrganisationLogin,
			//ExUI.manageOrgTC,
			ExUI.usersPage,
			ExUI.inviteUserPage
			.repeat(5,"n") {
				exec(ExUI.sendInvitation)
				},
			ExUI.manageOrganisationLogout
			)
	 }




	val EXUIMCaseCreationIACScn = scenario("***** IAC Create Case *****").repeat(1)
	{
	  	feed(feedUserDataIACCreate).feed(Feeders.IACCreateDataFeeder)
	  	.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
		//	.exec(EXUIMCLogin.termsnconditions)
		  	.repeat(2) {
					exec(EXUIIACMC.iaccasecreation)
						.exec(EXUIIACMC.shareacase)
				}

		.exec(EXUIMCLogin.manageCase_Logout)
	}


	/*val EXUIMCaseViewIACScn = scenario("***** IAC View Case *****").repeat(1)
	{
		feed(feedUserDataIACView).feed(Feeders.IACViewDataFeeder)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.manageCaseslogin)
			//.exec(EXUIMCLogin.termsnconditions)
			.exec(EXUIIACMC.findandviewcase)
			.exec(EXUIMCLogin.manageCase_Logout)
	}*/


	val EXUIMCaseCaseworkerScn = scenario("***** Caseworker Journey ******").repeat(1)
  {
		feed(feedUserDataCaseworker).feed(Feeders.CwDataFeeder)
			.exec(EXUIMCLogin.manageCasesHomePage)
			.exec(EXUIMCLogin.caseworkerLogin)
		.repeat(1) {
			exec(EXUICaseWorker.ApplyFilters)
			exec(EXUICaseWorker.ApplySort)
			exec(EXUICaseWorker.ClickFindCase)
			.exec(EXUICaseWorker.ViewCase)
			}
		.exec(EXUIMCLogin.manageCase_Logout)
  }


	setUp(
		EXUIMCaseCaseworkerScn.inject(nothingFor(5),rampUsers(1) during (1)),
		EXUIMCaseCreationIACScn.inject(nothingFor(15),rampUsers(1) during (1))
		//EXUIMCaseViewIACScn.inject(nothingFor(25),rampUsers(1) during (3))
	).protocols(IAChttpProtocol)

	/*setUp(
		EXUIScn.inject(rampUsers(1) during (10))
	).protocols(XUIHttpProtocol)*/
}
