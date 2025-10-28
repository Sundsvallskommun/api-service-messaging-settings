package se.sundsvall.messagingsettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;

@SpringBootTest
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
@Transactional
class MessagingSettingRepositoryTest {

	@Autowired
	private MessagingSettingRepository messagingSettingsRepository;

	@Autowired
	private FilterSpecificationConverter filterSpecificationConverter;

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

	@ParameterizedTest(name = "{0}")
	@MethodSource("findAllArgumentProvider")
	void findByAssignedGroupAndAssignedUserIdWhereExternalTagsIsNotEmpty(String testDescription, String filter, List<String> expectedMatchingIds) {

		final Specification<MessagingSettingEntity> specification = filterSpecificationConverter.convert(filter);
		final var result = messagingSettingsRepository.findAll(specification);

		assertThat(result).isNotEmpty().extracting(MessagingSettingEntity::getId)
			.asInstanceOf(InstanceOfAssertFactories.LIST)
			.isEqualTo(expectedMatchingIds);
	}

	private static Stream<Arguments> findAllArgumentProvider() {
		return Stream.of(
			Arguments.of("Search by namespace [NS1]", "values.key: 'namespace' and values.value: 'NS1'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281] and department id [400]", "municipalityId: '2281' and values.key: 'department_id' and values.value: '400'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7")),
			Arguments.of("Search by municipality [2281] and department name [dept46]", "municipalityId: 2281 and values.key: 'department_name' and values.value: 'dept46'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281] and namespace [NS1]", "municipalityId: 2281 and values.key: 'namespace' and values.value: 'NS1'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by municipality [2281]", "municipalityId: 2281", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of("Search by department id [404]", "values.key: 'department_id' and values.value: '404'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889c2")),
			Arguments.of("Search by department name [dept46]", "values.key: 'department_name' and values.value: 'dept46'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")),
			Arguments.of("Search by department name [dEPt46] to verify case insensitive matching", "values.key: 'Department_naME' and values.value: 'dEPt46'", List.of("475dcfd4-21d5-4f1d-9aac-fbf247f889b9")));
	}

	@Test
	void findAllWithNoFilter() {
		final List<MessagingSettingEntity> result = messagingSettingsRepository.findAll((Specification) null);

		assertThat(result).isNotEmpty().extracting(MessagingSettingEntity::getId)
			.asInstanceOf(InstanceOfAssertFactories.LIST)
			.isEqualTo(List.of(
				"475dcfd4-21d5-4f1d-9aac-fbf247f889b7", "475dcfd4-21d5-4f1d-9aac-fbf247f889b8", "475dcfd4-21d5-4f1d-9aac-fbf247f889b9",
				"475dcfd4-21d5-4f1d-9aac-fbf247f889c1", "475dcfd4-21d5-4f1d-9aac-fbf247f889c2", "475dcfd4-21d5-4f1d-9aac-fbf247f889c3"));

	}
}
