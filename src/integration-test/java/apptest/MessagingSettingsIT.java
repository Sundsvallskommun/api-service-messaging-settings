package apptest;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.messagingsettings.Application;

@ActiveProfiles("it")
@WireMockAppTestSuite(files = "classpath:/MessagingSettingsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-MessagingSettingsIT.sql"
})
class MessagingSettingsIT extends AbstractAppTest {

	private static final String HEADER_SENT_BY_VALUE = "TestUser; type=adAccount";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_senderInfoByDeparmentId() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/sender-info?departmentId=1")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_senderInfoNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/sender-info?departmentId=NON_EXISTING_DEPARTMENT_ID")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_senderInfoBadRequest() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/NOT_VALID_MUNICIPALITY_ID/sender-info")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_callbackEmail() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/2/callback-email")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_callbackEmailNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/NON_EXISTING_DEPARTMENT_ID/callback-email")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_callbackEmailBadRequest() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/NOT_VALID_MUNICIPALITY_ID/NON_EXISTING_DEPARTMENT_ID/callback-email")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_portalSettings() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/portal-settings")
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_portalSettingsNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/portal-settings")
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_portalSettingsBadRequest() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/NOT_VALID_MUNICIPALITY_ID/portal-settings")
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_senderInfoByNamespace() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/sender-info?namespace=NS2")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_senderInfoByNamespaceAndDepartmentName() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/sender-info?namespace=NS2&departmentName=conversation")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_fetchMessagingSettingsWithNoFilters() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test13_fetchMessagingSettingsFilteredByNamespace() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=values.key:'namespace' and values.value:'NS2'")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test14_fetchMessagingSettingsFilteredByNamespaceAndDepartmentName() {
		// the exists function in filter is needed when evaluating multiple expressions in valuesettings-list to create a sub
		// query for each expression otherwise the expression will always return emtpy result as a key for example can not both
		// be 'namespace' and 'department_name'
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=exists(values.key:'namespace' and values.value:'NS2') and exists(values.key:'department_name' and values.value:'paratransit')")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test15_fetchMessagingSettingsFilteredByDepartmentId() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=values.key:'department_id' and values.value:'3'")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test16_fetchUserMessagingSettings() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/user")
			.withHeader(Identifier.HEADER_NAME, "joe01doe; type=adAccount")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

}
