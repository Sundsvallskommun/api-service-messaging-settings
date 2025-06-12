package se.sundsvall.messagingsettings.integration.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.employee.PortalPersonData;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.messagingsettings.integration.employee.mapper.EmployeeMapper;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

@ExtendWith(MockitoExtension.class)
class EmployeeIntegrationTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String DOMAIN_PERSONAL = "PERSONAL";
	private static final String LOGIN_NAME = "bobfromhr";

	@Mock
	private EmployeeClient mockEmployeeClient;

	@InjectMocks
	private EmployeeIntegration employeeIntegration;

	private static Stream<Arguments> argumentProvider() {
		return Stream.of(
			Arguments.of("1|123|org1¤2|456|dept1", new DepartmentInfo("2", "456", "dept1")),
			Arguments.of("1¤2|789|dept2", new DepartmentInfo("2", "789", "dept2")));
	}

	@ParameterizedTest
	@MethodSource("argumentProvider")
	void getDepartmentInfo(String orgTree, DepartmentInfo expectedDepartmentInfo) {
		final var portalPersonData = new PortalPersonData();
		portalPersonData.setOrgTree(orgTree);

		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.of(portalPersonData));

		try (MockedStatic<EmployeeMapper> employeeMapperMock = Mockito.mockStatic(EmployeeMapper.class)) {
			employeeMapperMock.when(() -> EmployeeMapper.toDepartmentInfo(orgTree))
				.thenReturn(expectedDepartmentInfo);

			final var result = employeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);

			assertThat(result).isPresent();
			assertThat(result).contains(expectedDepartmentInfo);

			verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
			verifyNoMoreInteractions(mockEmployeeClient);
			employeeMapperMock.verify(() -> EmployeeMapper.toDepartmentInfo(orgTree));
			employeeMapperMock.verifyNoMoreInteractions();
		}
	}

	@Test
	void getDepartmentInfo_withEmptyOrgTree() {
		final var portalPersonData = new PortalPersonData();

		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.of(portalPersonData));

		final var result = employeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);

		assertThat(result).isEmpty();

		verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeClient);
	}

	@Test
	void getDepartmentInfo_withNoDepartments() {
		final var portalPersonData = new PortalPersonData();

		portalPersonData.setOrgTree("1|123|org1");

		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.of(portalPersonData));

		final var result = employeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);

		assertThat(result).isEmpty();

		verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeClient);
	}

	@Test
	void getDepartmentInfo_withNoEmployee() {
		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.empty());

		final var result = employeeIntegration.getDepartmentInfo(MUNICIPALITY_ID, LOGIN_NAME);

		assertThat(result).isEmpty();

		verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeClient);
	}
}
