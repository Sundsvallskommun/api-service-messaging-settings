package se.sundsvall.messagingsettings.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.validation.ValidIdentifier;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.service.MessagingSettingsService;

@Tag(name = "Messaging Settings")
@RestController
@Validated
@RequestMapping("/{municipalityId}")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class,
})))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class MessagingSettingsResource {

	private final MessagingSettingsService messagingSettingsService;

	MessagingSettingsResource(final MessagingSettingsService messagingSettingsService) {
		this.messagingSettingsService = messagingSettingsService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get messaging settings", description = "Get all messaging settings or the ones that matches provided filter", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	})
	ResponseEntity<List<MessagingSettings>> fetchMessagingSettings(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "filter",
			description = "Syntax description: [spring-filter](https://github.com/turkraft/spring-filter/blob/85730f950a5f8623159cc0eb4d737555f9382bb7/README.md#syntax)",
			example = "created > '2022-09-08T12:00:00.000+02:00' and values.key: 'namespace' and values.value: 'NS1'",
			schema = @Schema(implementation = String.class)) @Nullable @Filter final Specification<MessagingSettingEntity> filter) {

		return ok(messagingSettingsService.fetchMessagingSettings(municipalityId, filter));
	}

	@GetMapping(path = "/user", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get messaging settings for organization connected to a user", description = "Get messaging settings for the organization connected to the provided user.", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<List<MessagingSettings>> getMessagingSettingsForUser(
		@Parameter(name = Identifier.HEADER_NAME, description = "User identity", example = "joe01doe;type=adAccount") @RequestHeader(name = Identifier.HEADER_NAME) @NotNull @ValidIdentifier final String xSentBy,
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable(name = "municipalityId") final String municipalityId) {

		return ok(messagingSettingsService.fetchMessagingSettingsForUser(municipalityId, Identifier.parse(xSentBy)));
	}
}
