package se.sundsvall.messagingsettings.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.PortalSettingsResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@Tag(name = "Messaging Settings")
@RestController
@Validated
@RequestMapping("/{municipalityId}")
@ApiResponse(responseCode = "400",
	description = "Bad Request",
	content = @Content(
		mediaType = APPLICATION_PROBLEM_JSON_VALUE,
		schema = @Schema(oneOf = {
			Problem.class, ConstraintViolationProblem.class,
		})))
@ApiResponse(responseCode = "500",
	description = "Internal Server Error",
	content = @Content(
		mediaType = APPLICATION_PROBLEM_JSON_VALUE,
		schema = @Schema(implementation = Problem.class)))
class MessagingSettingsResource {

	private final MessagingSettingsService messagingSettingsService;

	MessagingSettingsResource(final MessagingSettingsService messagingSettingsService) {
		this.messagingSettingsService = messagingSettingsService;
	}

	@GetMapping(path = "/{departmentId}/sender-info", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get sender info", description = "Get sender info for given department and municipality.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	ResponseEntity<SenderInfoResponse> getSenderInfo(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "departmentId", description = "Department ID", example = "SKM") @PathVariable(name = "departmentId") final String departmentId) {
		return ok(messagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId));
	}

	@GetMapping(path = "/{namespace}/sender-infos", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get sender info", description = "Get sender information for a given municipality and namespace.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	ResponseEntity<List<SenderInfoResponse>> getSenderInfoByNamespace(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "SKM") @PathVariable(name = "namespace") final String namespace) {
		return ok(messagingSettingsService.getSenderInfoByMunicipalityIdAndNamespace(municipalityId, namespace));
	}

	@GetMapping("/{namespace}/{departmentName}/sender-info")
	ResponseEntity<SenderInfoResponse> getSenderInfoByNamespaceAndDepartmentName(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "namespace", description = "Namespace", example = "SKM") @PathVariable(name = "namespace") final String namespace,
		@Parameter(name = "departmentName", description = "Department name", example = "Sundsvalls kommun") @PathVariable(name = "departmentName") final String departmentName) {

		return ok(messagingSettingsService.getSenderInfoByMunicipalityIdAndNamespaceAndDepartmentName(municipalityId, namespace, departmentName));
	}

	@GetMapping(path = "/{departmentId}/callback-email", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get callback email", description = "Get callback e-mail for given department and municipality.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	ResponseEntity<CallbackEmailResponse> getCallbackEmail(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "departmentId", description = "Department ID", example = "SKM") @PathVariable(name = "departmentId") final String departmentId) {
		return ok(messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(municipalityId, departmentId));
	}

	@GetMapping(path = "/portal-settings", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get portal settings", description = "Get portal settings for given department.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	ResponseEntity<PortalSettingsResponse> getPortalSettings(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId) {
		final var user = messagingSettingsService.getUser();
		return ok(messagingSettingsService.getPortalSettings(municipalityId, user));
	}
}
