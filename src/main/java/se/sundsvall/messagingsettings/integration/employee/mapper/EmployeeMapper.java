package se.sundsvall.messagingsettings.integration.employee.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

public final class EmployeeMapper {

	private static final String ORGANIZATION_DELIMITER = "¤";
	private static final String INFORMATION_DELIMITER = "\\|";

	private EmployeeMapper() {}

	private static DepartmentInfo mapToDepartmentInfo(final String departmentString) {
		final var orgInfo = departmentString.split(INFORMATION_DELIMITER);

		if (orgInfo.length != 3) {
			return null;
		}

		final var level = orgInfo[0];
		final var orgId = orgInfo[1];
		final var name = orgInfo[2];

		return new DepartmentInfo(level, orgId, name);
	}

	public static List<DepartmentInfo> toDepartmentInfos(final String organizationsString) {
		if (organizationsString == null) {
			return Collections.emptyList();
		}
		final var departmentStrings = organizationsString.split(ORGANIZATION_DELIMITER);

		final int limit = Math.min(2, departmentStrings.length);

		return Arrays.stream(departmentStrings)
			.limit(limit)
			.map(EmployeeMapper::mapToDepartmentInfo)
			.filter(Objects::nonNull)
			.toList()
			.reversed(); // Reverses the list to have level 2 first and level 1 second
	}
}
