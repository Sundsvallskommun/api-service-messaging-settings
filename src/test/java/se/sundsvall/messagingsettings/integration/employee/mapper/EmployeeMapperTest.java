package se.sundsvall.messagingsettings.integration.employee.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

	@Test
	void toDepartmentInfos_withMultiLevelOrganization() {
		final var orgTree = "1|123|org1¤2|456|dept1";
		final var result = EmployeeMapper.toDepartmentInfos(orgTree);

		assertThat(result).hasSize(2);
		// Reversed: level 2 first, level 1 second
		assertThat(result.get(0)).isEqualTo(new DepartmentInfo("2", "456", "dept1"));
		assertThat(result.get(1)).isEqualTo(new DepartmentInfo("1", "123", "org1"));
	}

	@Test
	void toDepartmentInfos_withThreeLevels_limitedToTwo() {
		final var orgTree = "1|111|org1¤2|222|dept1¤3|333|subdept1";
		final var result = EmployeeMapper.toDepartmentInfos(orgTree);

		assertThat(result).hasSize(2);
		// Only first 2 levels, reversed
		assertThat(result.get(0)).isEqualTo(new DepartmentInfo("2", "222", "dept1"));
		assertThat(result.get(1)).isEqualTo(new DepartmentInfo("1", "111", "org1"));
	}

	@Test
	void toDepartmentInfos_withSingleLevelOrganization() {
		final var orgTree = "1|123|org1";
		final var result = EmployeeMapper.toDepartmentInfos(orgTree);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isEqualTo(new DepartmentInfo("1", "123", "org1"));
	}

	@Test
	void toDepartmentInfos_withIncompleteDepartmentData() {
		// The first entry is incomplete (missing parts), the second is valid
		final var orgTree = "1¤2|456|dept1";
		final var result = EmployeeMapper.toDepartmentInfos(orgTree);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isEqualTo(new DepartmentInfo("2", "456", "dept1"));
	}

	@Test
	void toDepartmentInfos_withNullInput() {
		final var result = EmployeeMapper.toDepartmentInfos(null);
		assertThat(result).isEmpty();
	}

	@Test
	void toDepartmentInfos_withEmptyInput() {
		final var result = EmployeeMapper.toDepartmentInfos("");
		assertThat(result).isEmpty();
	}
}
