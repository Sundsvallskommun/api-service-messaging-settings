package se.sundsvall.messagingsettings.integration.employee;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.messagingsettings.integration.employee.configuration.EmployeeConfiguration.CLIENT_ID;

import generated.se.sundsvall.employee.PortalPersonData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.messagingsettings.integration.employee.configuration.EmployeeConfiguration;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.employee.url}", configuration = EmployeeConfiguration.class, dismiss404 = true)
public interface EmployeeClient {

	/**
	 * Get a specific employee by domain and loginname
	 *
	 * @param  municipalityId the municipalityId to use in search
	 * @param  domain         the domain to use in search
	 * @param  loginname      login name to use in search
	 * @return                {@link Optional<PortalPersonData>} with possible information about the employee
	 */
	@GetMapping(path = "/{municipalityId}/portalpersondata/{domain}/{loginname}", produces = APPLICATION_JSON_VALUE)
	Optional<PortalPersonData> getEmployeeByDomainAndLoginName(
		@PathVariable String municipalityId,
		@PathVariable String domain,
		@PathVariable String loginname);
}
