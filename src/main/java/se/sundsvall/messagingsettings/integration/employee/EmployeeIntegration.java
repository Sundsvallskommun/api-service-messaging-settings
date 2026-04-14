package se.sundsvall.messagingsettings.integration.employee;

import generated.se.sundsvall.employee.PortalPersonData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.messagingsettings.integration.employee.configuration.EmployeeProperties;
import se.sundsvall.messagingsettings.integration.employee.mapper.EmployeeMapper;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Wrapper class for {@link EmployeeClient}.
 */
@Component
public class EmployeeIntegration {

	private final EmployeeClient employeeClient;
	private final EmployeeProperties employeeProperties;

	public EmployeeIntegration(final EmployeeClient employeeClient, final EmployeeProperties employeeProperties) {
		this.employeeClient = employeeClient;
		this.employeeProperties = employeeProperties;
	}

	public List<DepartmentInfo> getDepartmentInfos(final String municipalityId, final String loginName) {
		final var domain = resolveDomain(municipalityId);
		return employeeClient.getEmployeeByDomainAndLoginName(municipalityId, domain, loginName)
			.map(PortalPersonData::getFullOrgTree)
			.map(EmployeeMapper::toDepartmentInfos)
			.orElseGet(List::of);
	}

	private String resolveDomain(final String municipalityId) {
		return Optional.ofNullable(employeeProperties.domains())
			.map(domains -> domains.get(municipalityId))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, "No employee domain configured for municipality '%s'.".formatted(municipalityId)));
	}
}
