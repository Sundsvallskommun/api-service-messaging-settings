package apptest;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.messagingsettings.Application;
import se.sundsvall.messagingsettings.test.annotation.IntegrationTest;

@IntegrationTest
@WireMockAppTestSuite(files = "classpath:/MessagingSettingsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-MessagingSettingsIT.sql"
})
class MessagingSettingsIT extends AbstractAppTest {

	private static final String SERVICE_PATH = "/{municipalityId}/{departmentId}/sender-info";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test1_senderInfo_OK() {
		final var municipalityId = "2281";
		final var departmentId = "SKM";

		setupCall()
			.withHttpMethod(GET)
			.withServicePath(uriBuilder -> uriBuilder
				.replacePath(SERVICE_PATH)
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
				.replacePath(SERVICE_PATH)
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
				.replacePath(SERVICE_PATH)
				.build(Map.of("municipalityId", municipalityId, "departmentId", departmentId)))
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}
}