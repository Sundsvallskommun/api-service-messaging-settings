package se.sundsvall.messagingsettings.integration.employee;

import generated.se.sundsvall.employee.PortalPersonData;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.messagingsettings.integration.employee.mapper.EmployeeMapper;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

/**
 * Wrapper class for {@link EmployeeClient}.
 */
@Component
public class EmployeeIntegration {

	private static final String DOMAIN_PERSONAL = "PERSONAL";

	private final EmployeeClient employeeClient;

	public EmployeeIntegration(final EmployeeClient employeeClient) {
		this.employeeClient = employeeClient;
	}

	public List<DepartmentInfo> getDepartmentInfos(final String municipalityId, final String loginName) {
		return employeeClient.getEmployeeByDomainAndLoginName(municipalityId, DOMAIN_PERSONAL, loginName)
			.map(PortalPersonData::getFullOrgTree)
			.map(EmployeeMapper::toDepartmentInfos)
			.orElseGet(List::of);
	}
}
