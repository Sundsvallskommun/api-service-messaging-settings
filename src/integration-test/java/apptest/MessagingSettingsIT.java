package apptest;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
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

	private static final String SENDER_INFO_SERVICE_PATH = "/{municipalityId}/{departmentId}/sender-info";
	private static final String CALLBACK_EMAIL_SERVICE_PATH = "/{municipalityId}/{departmentId}/callback-email";
	private static final String PORTAL_SETTINGS_SERVICE_PATH = "/{municipalityId}/portal-settings";

	private static final String HEADER_SENT_BY_VALUE = "TestUser; type=adAccount";

	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test1_senderInfo_OK() {
		final var municipalityId = "2281";
		final var departmentId = "SKM";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(SENDER_INFO_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_senderInfo_NotFound() {
		final var municipalityId = "2281";
		final var departmentId = "NON_EXISTING_DEPARTMENT_ID";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(SENDER_INFO_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_senderInfo_BadRequest() {
		final var municipalityId = "NON_EXISTING_MUNICIPALITY_ID";
		final var departmentId = "NON_EXISTING_DEPARTMENT_ID";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(SENDER_INFO_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_callbackEmail_OK() {
		final var municipalityId = "2281";
		final var departmentId = "SKM";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(CALLBACK_EMAIL_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test5_callbackEmail_NotFound() {
		final var municipalityId = "2281";
		final var departmentId = "NON_EXISTING_DEPARTMENT_ID";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(CALLBACK_EMAIL_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test6_callbackEmail_BadRequest() {
		final var municipalityId = "NON_EXISTING_MUNICIPALITY_ID";
		final var departmentId = "NON_EXISTING_DEPARTMENT_ID";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(CALLBACK_EMAIL_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test7_portalSettings_OK() {
		final var municipalityId = "2281";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(PORTAL_SETTINGS_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId)))
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test8_portalSettings_NotFound() {
		final var municipalityId = "2281";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(PORTAL_SETTINGS_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId)))
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test9_portalSettings_BadRequest() {
		final var municipalityId = "NON_EXISTING_MUNICIPALITY_ID";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(PORTAL_SETTINGS_SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId)))
			.withHeader(Identifier.HEADER_NAME, HEADER_SENT_BY_VALUE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_senderInfo_namespace_OK() {
		final var municipalityId = "2281";
		final var namespace = "SBK";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath("/{municipalityId}/{namespace}/sender-infos")
				.build(Map.of("municipalityId", municipalityId, "namespace", namespace)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_senderInfo_namespace_BadRequest() {
		final var municipalityId = "INVALID_MUNICIPALITY";
		final var namespace = "SBK";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath("/{municipalityId}/{namespace}/sender-infos")
				.build(Map.of("municipalityId", municipalityId, "namespace", namespace)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_senderInfo_namespace_departmentName_OK() {
		final var municipalityId = "2281";
		final var namespace = "SBK";
		final var departmentName = "dept44";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath("/{municipalityId}/{namespace}/{departmentName}/sender-info")
				.build(Map.of("municipalityId", municipalityId, "namespace", namespace, "departmentName", departmentName)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();
	}
}
