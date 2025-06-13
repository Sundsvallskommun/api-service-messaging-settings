package se.sundsvall.messagingsettings.integration.employee.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

	private static final String ORG_TREE_SINGLE_LEVEL = "1|123|org1";
	private static final String ORG_TREE_MULTI_LEVEL = "1|123|org1¤2|456|dept1";
	private static final String ORG_TREE_MULTI_LEVEL_INCOMPLETE = "1¤2|456|dept1";

	@Test
	void toDepartmentInfo_withSingleLevelOrganization() {
		final var result = EmployeeMapper.toDepartmentInfo(ORG_TREE_SINGLE_LEVEL);

		assertThat(result).isNull();
	}

	@Test
	void toDepartmentInfo_withMultiLevelOrganization() {
		final var result = EmployeeMapper.toDepartmentInfo(ORG_TREE_MULTI_LEVEL);

		assertThat(result).isNotNull();
		assertThat(result.level()).isEqualTo("2");
		assertThat(result.id()).isEqualTo("456");
		assertThat(result.name()).isEqualTo("dept1");
	}

	@Test
	void toDepartmentInfo_withMultiLevelOrganizationIncomplete() {
		final var result = EmployeeMapper.toDepartmentInfo(ORG_TREE_MULTI_LEVEL_INCOMPLETE);

		assertThat(result).isNotNull();
		assertThat(result.level()).isEqualTo("2");
		assertThat(result.id()).isEqualTo("456");
		assertThat(result.name()).isEqualTo("dept1");
	}

	@Test
	void toDepartmentInfo_withNullInput() {
		final var result = EmployeeMapper.toDepartmentInfo(null);

		assertThat(result).isNull();
	}

	@Test
	void toDepartmentInfo_withEmptyInput() {
		final var result = EmployeeMapper.toDepartmentInfo("");

		assertThat(result).isNull();
	}
}
