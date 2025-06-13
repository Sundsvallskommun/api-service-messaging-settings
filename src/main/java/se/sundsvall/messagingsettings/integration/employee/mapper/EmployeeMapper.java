package se.sundsvall.messagingsettings.integration.employee.mapper;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

public class EmployeeMapper {

	private static final String ORGANIZATION_DELIMITER = "¤";
	private static final String INFORMATION_DELIMITER = "\\|";

	private EmployeeMapper() {}

	public static DepartmentInfo toDepartmentInfo(String organizationsString) {
		return ofNullable(organizationsString)
			.map(EmployeeMapper::parseOrganisationString)
			.orElse(null);
	}

	private static DepartmentInfo parseOrganisationString(String organizationsString) {
		final var organizations = organizationsString.split(ORGANIZATION_DELIMITER);

		for (final String organization : organizations) {
			final var orgInfo = organization.split(INFORMATION_DELIMITER);

			if (orgInfo.length != 3) {
				continue;
			}

			final var level = orgInfo[0];
			final var orgId = orgInfo[1];
			final var name = orgInfo[2];

			if (toInt(level) == 2) {
				return new DepartmentInfo(level, orgId, name);
			}
		}

		return null;
	}
}
