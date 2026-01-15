package apptest;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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

	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_fetchMessagingSettingsWithNoFilters() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_fetchMessagingSettingsFilteredByNamespace() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=values.key:'namespace' and values.value:'NS2'")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_fetchMessagingSettingsFilteredByNamespaceAndDepartmentName() {
		// the exists function in filter is needed when evaluating multiple expressions in value-settings-list to create a sub
		// query for each expression, otherwise the expression will always return an empty result as a key. For example, cannot both
		// be 'namespace' and 'department_name'
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=exists(values.key:'namespace' and values.value:'NS2') and exists(values.key:'department_name' and values.value:'paratransit')")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_fetchMessagingSettingsFilteredByDepartmentId() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281?filter=values.key:'department_id' and values.value:'3'")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_fetchUserMessagingSettings() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/user")
			.withHeader("X-Sent-By", "joe01doe; type=adAccount")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_createMessagingSetting() {
		setupCall()
			.withHttpMethod(POST)
			.withServicePath("/2281")
			.withRequest("request.json")
			.withExpectedResponseStatus(CREATED)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_createMessagingSettingWithInvalidMunicipalityId() {
		setupCall()
			.withHttpMethod(POST)
			.withServicePath("/INVALID_MUNICIPALITY")
			.withRequest("request.json")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_getMessagingSettingById() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/475dcfd4-21d5-4f1d-9aac-fbf247f889b1")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_getMessagingSettingByIdNotFound() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/550e8400-e29b-41d4-a716-446655440099")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_getMessagingSettingByIdWrongMunicipality() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/9999/475dcfd4-21d5-4f1d-9aac-fbf247f889b1")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_getMessagingSettingByIdInvalidUuid() {
		setupCall()
			.withHttpMethod(GET)
			.withServicePath("/2281/not-a-uuid")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_updateMessagingSetting() {
		setupCall()
			.withHttpMethod(PATCH)
			.withServicePath("/2281/475dcfd4-21d5-4f1d-9aac-fbf247f889b2")
			.withRequest("request.json")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test13_updateMessagingSettingNotFound() {
		setupCall()
			.withHttpMethod(PATCH)
			.withServicePath("/2281/550e8400-e29b-41d4-a716-446655440099")
			.withRequest("request.json")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test14_updateMessagingSettingInvalidUuid() {
		setupCall()
			.withHttpMethod(PATCH)
			.withServicePath("/2281/not-a-uuid")
			.withRequest("request.json")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test15_deleteMessagingSetting() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/475dcfd4-21d5-4f1d-9aac-fbf247f889b3")
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test16_deleteMessagingSettingNotFound() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/550e8400-e29b-41d4-a716-446655440099")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test17_deleteMessagingSettingInvalidUuid() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/not-a-uuid")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test18_deleteMessagingSettingKey() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/475dcfd4-21d5-4f1d-9aac-fbf247f889b6/key/sms_sender")
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test19_deleteMessagingSettingKeyNotFound() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/475dcfd4-21d5-4f1d-9aac-fbf247f889b7/key/non_existent_key")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test20_deleteMessagingSettingKeyEntityNotFound() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/550e8400-e29b-41d4-a716-446655440099/key/some_key")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test21_deleteMessagingSettingKeyInvalidUuid() {
		setupCall()
			.withHttpMethod(DELETE)
			.withServicePath("/2281/not-a-uuid/key/some_key")
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

}
