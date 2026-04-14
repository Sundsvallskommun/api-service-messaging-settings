package se.sundsvall.messagingsettings.integration.employee;

import generated.se.sundsvall.employee.PortalPersonData;
import java.util.List;
import java.util.Map;
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
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.messagingsettings.integration.employee.configuration.EmployeeProperties;
import se.sundsvall.messagingsettings.integration.employee.mapper.EmployeeMapper;
import se.sundsvall.messagingsettings.service.model.DepartmentInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeIntegrationTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String DOMAIN_PERSONAL = "PERSONAL";
	private static final String LOGIN_NAME = "someValue";

	@Mock
	private EmployeeClient mockEmployeeClient;

	@Mock
	private EmployeeProperties mockEmployeeProperties;

	@InjectMocks
	private EmployeeIntegration employeeIntegration;

	private static Stream<Arguments> argumentProvider() {
		return Stream.of(
			// Two levels, complete data
			Arguments.of("1|123|org1¤2|456|dept1", List.of(new DepartmentInfo("2", "456", "dept1"), new DepartmentInfo("1", "123", "org1"))),
			// Two levels, incomplete first level (filtered out)
			Arguments.of("1¤2|789|dept2", List.of(new DepartmentInfo("2", "789", "dept2"))),
			// Three levels - only the first two returned, reversed
			Arguments.of("1|111|org1¤2|222|dept1¤3|333|subdept1", List.of(new DepartmentInfo("2", "222", "dept1"), new DepartmentInfo("1", "111", "org1"))),
			// Single level
			Arguments.of("1|123|org1", List.of(new DepartmentInfo("1", "123", "org1"))));
	}

	@ParameterizedTest
	@MethodSource("argumentProvider")
	void getDepartmentInfos(final String orgTree, final List<DepartmentInfo> expectedDepartmentInfos) {
		final var portalPersonData = new PortalPersonData();
		portalPersonData.setFullOrgTree(orgTree);

		when(mockEmployeeProperties.domains()).thenReturn(Map.of(MUNICIPALITY_ID, DOMAIN_PERSONAL));
		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.of(portalPersonData));

		try (final MockedStatic<EmployeeMapper> employeeMapperMock = Mockito.mockStatic(EmployeeMapper.class)) {
			employeeMapperMock.when(() -> EmployeeMapper.toDepartmentInfos(orgTree))
				.thenReturn(expectedDepartmentInfos);

			final var result = employeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);

			assertThat(result).isNotEmpty();
			assertThat(result).isEqualTo(expectedDepartmentInfos);

			verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
			verifyNoMoreInteractions(mockEmployeeClient);
			employeeMapperMock.verify(() -> EmployeeMapper.toDepartmentInfos(orgTree));
			employeeMapperMock.verifyNoMoreInteractions();
		}
	}

	@Test
	void getDepartmentInfos_withEmptyOrgTree() {
		final var portalPersonData = new PortalPersonData();

		when(mockEmployeeProperties.domains()).thenReturn(Map.of(MUNICIPALITY_ID, DOMAIN_PERSONAL));
		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.of(portalPersonData));

		final var result = employeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);

		assertThat(result).isEmpty();

		verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeClient);
	}

	@Test
	void getDepartmentInfos_withNoEmployee() {
		when(mockEmployeeProperties.domains()).thenReturn(Map.of(MUNICIPALITY_ID, DOMAIN_PERSONAL));
		when(mockEmployeeClient.getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME))
			.thenReturn(Optional.empty());

		final var result = employeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME);

		assertThat(result).isEmpty();

		verify(mockEmployeeClient).getEmployeeByDomainAndLoginName(MUNICIPALITY_ID, DOMAIN_PERSONAL, LOGIN_NAME);
		verifyNoMoreInteractions(mockEmployeeClient);
	}

	@Test
	void getDepartmentInfos_withUnconfiguredMunicipality() {
		when(mockEmployeeProperties.domains()).thenReturn(Map.of("2281", DOMAIN_PERSONAL));

		assertThatThrownBy(() -> employeeIntegration.getDepartmentInfos("9999", LOGIN_NAME))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No employee domain configured for municipality '9999'.");

		verifyNoInteractions(mockEmployeeClient);
	}

	@Test
	void getDepartmentInfos_withNullDomains() {
		when(mockEmployeeProperties.domains()).thenReturn(null);

		assertThatThrownBy(() -> employeeIntegration.getDepartmentInfos(MUNICIPALITY_ID, LOGIN_NAME))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No employee domain configured for municipality '2281'.");

		verifyNoInteractions(mockEmployeeClient);
	}
}
