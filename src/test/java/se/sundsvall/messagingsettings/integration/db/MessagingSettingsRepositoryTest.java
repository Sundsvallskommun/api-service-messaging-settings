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
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

@DataJpaTest
@ActiveProfiles("junit")
@AutoConfigureTestDatabase(replace = NONE)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
class MessagingSettingsRepositoryTest {

	@Autowired
	private MessagingSettingsRepository messagingSettingsRepository;

	@ParameterizedTest
	@MethodSource("findAllBySpecificationArgumentProvider")
	void findAllBySpecification(String municipalityId, String departmentId, String departmentName, String namespace, List<String> expectedMatchingIds) {

		final var result = messagingSettingsRepository.findAllBySpecification(municipalityId, departmentId, departmentName, namespace);

		assertThat(result).isNotEmpty().extracting(MessagingSettingsEntity::getId)
			.asInstanceOf(InstanceOfAssertFactories.LIST)
			.isEqualTo(expectedMatchingIds);

	}

	private static Stream<Arguments> findAllBySpecificationArgumentProvider() {
		return Stream.of(
			Arguments.of(null, null, null, "NS1", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("2281", "400", null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7")),
			Arguments.of("2281", null, "dept46", null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("2281", null, null, "NS1", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("2281", null, null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of(null, "404", null, null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of(null, null, "dept46", null, List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of(null, null, null, "NS1", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of(null, null, null, null, List.of(
				"475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9",
				"475dcfd4-21d5-4f1d-9aac-fbf247f889c1", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2", "475dcfd4-21d5-4f1d-9aac-fbf247f889c3"))

		);
	}

	@Test
	void findByMunicipalityIdAndDepartmentId_notFound() {
		final var municipalityId = "NON_EXISTING";
		final var departmentId = "NON_EXISTING";

		assertThat(messagingSettingsRepository.findAllBySpecification(municipalityId, departmentId, null, null)).isEmpty();
	}
}
