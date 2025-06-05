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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.messagingsettings.api.model.CallbackEmailResponse;
import se.sundsvall.messagingsettings.api.model.SenderInfoResponse;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@Tag(name = "Messaging Settings")
@RestController
@Validated
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

	static final String GET_SENDER_INFO_PATH = "/{municipalityId}/{departmentId}/sender-info";
	static final String GET_CALLBACK_EMAIL_PATH = "/{municipalityId}/{departmentId}/callback-email";

	private final MessagingSettingsService messagingSettingsService;

	MessagingSettingsResource(MessagingSettingsService messagingSettingsService) {
		this.messagingSettingsService = messagingSettingsService;
	}

	@GetMapping(path = GET_SENDER_INFO_PATH, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get sender info", description = "Get sender info for given department and municipality.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	ResponseEntity<SenderInfoResponse> getSenderInfo(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "departmentId", description = "Department ID", example = "SKM") @PathVariable(name = "departmentId") final String departmentId) {
		return ok(messagingSettingsService.getSenderInfoByMunicipalityIdAndDepartmentId(municipalityId, departmentId));
	}

	@GetMapping(path = GET_CALLBACK_EMAIL_PATH, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get callback email", description = "Get callback e-mail for given department and municipality.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
	})
	ResponseEntity<CallbackEmailResponse> getCallbackEmail(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId,
		@Parameter(name = "departmentId", description = "Department ID", example = "SKM") @PathVariable(name = "departmentId") final String departmentId) {
		return ok(messagingSettingsService.getCallbackEmailByMunicipalityIdAndDepartmentId(municipalityId, departmentId));
	}
}
