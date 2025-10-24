package se.sundsvall.messagingsettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

@DataJpaTest
@ActiveProfiles("junit")
@AutoConfigureTestDatabase(replace = NONE)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
class MessagingSettingRepositoryTest {

	@Autowired
	private MessagingSettingRepository messagingSettingsRepository;

	@ParameterizedTest(name = "{0}")
	@MethodSource("findAllBySpecificationArgumentProvider")
	void findAllBySpecification(String testDescription, String municipalityId, String departmentId, String departmentName, String namespace, List<String> expectedMatchingIds) {

		final var result = messagingSettingsRepository.findAllBySpecification(municipalityId, departmentId, departmentName, namespace);

		assertThat(result).isNotEmpty().extracting(MessagingSettingEntity::getId)
			.asInstanceOf(InstanceOfAssertFactories.LIST)
			.isEqualTo(expectedMatchingIds);

	}

	private static Stream<Arguments> findAllBySpecificationArgumentProvider() {
		return Stream.of(
			Arguments.of("Search by namespace [NS1]", null, null, null, "NS1", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281] and department id [400]", "2281", "400", null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7")),
			Arguments.of("Search by municipality [2281] and department name [dept46]", "2281", null, "dept46", null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281] and namespace [NS1]", "2281", null, null, "NS1", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281]", "2281", null, null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of("Search by department id [404]", null, "404", null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of("Search by department name [dept46]", null, null, "dept46", null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by department name [dEPt46] to verify case insensitive matching", null, null, "dEPt46", null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search with no filters", null, null, null, null, List.of(
				"475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9",
				"475dcfd4-21d5-4f1d-9aac-fbf247f889c1", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2", "475dcfd4-21d5-4f1d-9aac-fbf247f889c3")));
	}

	@Test
	void findByMunicipalityIdAndDepartmentId_notFound() {
		final var municipalityId = "NON_EXISTING";
		final var departmentId = "NON_EXISTING";

		assertThat(messagingSettingsRepository.findAllBySpecification(municipalityId, departmentId, null, null)).isEmpty();
	}
}
