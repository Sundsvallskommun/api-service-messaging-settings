package se.sundsvall.messagingsettings.api;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.support.Identifier;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
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
		@Parameter(name = "filter",
			description = "Syntax description: [spring-filter](https://github.com/turkraft/spring-filter/blob/85730f950a5f8623159cc0eb4d737555f9382bb7/README.md#syntax)",
			example = "created > '2022-09-08T12:00:00.000+02:00' and values.key: 'namespace' and values.value: 'NS1'",
			schema = @Schema(implementation = String.class)) @Nullable @Filter final Specification<MessagingSettingEntity> filter,
		@Parameter(name = Identifier.HEADER_NAME, description = "User identity", example = "joe01doe;type=adAccount") @RequestHeader(name = Identifier.HEADER_NAME) @NotNull @ValidIdentifier final String xSentBy,
		@PathVariable @Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId final String municipalityId) {

		return ok(messagingSettingsService.fetchMessagingSettingsForUser(municipalityId, Identifier.get(), filter));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Create messaging setting", description = "Create a new messaging setting", responses = {
		@ApiResponse(responseCode = "201", description = "Created", headers = @Header(name = LOCATION, schema = @Schema(type = "string"))),
		@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
			Problem.class, ConstraintViolationProblem.class
		})))
	})
	ResponseEntity<Void> createMessagingSetting(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Valid @NotNull @RequestBody final MessagingSettingsRequest request,
		final UriComponentsBuilder uriComponentsBuilder) {

		final var result = messagingSettingsService.createMessagingSetting(municipalityId, request);
		return created(uriComponentsBuilder.path("/{municipalityId}/{id}").buildAndExpand(municipalityId, result.getId()).toUri()).build();
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get messaging setting by ID", description = "Get a specific messaging setting by ID", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<MessagingSettings> getMessagingSettingById(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Messaging setting ID", example = "c9383d10-6fb5-4fc1-bd0a-50bf5a24d5b7") @ValidUuid @PathVariable final String id) {

		return ok(messagingSettingsService.getMessagingSettingById(municipalityId, id));
	}

	@PatchMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Update messaging setting", description = "Update an existing messaging setting", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<MessagingSettings> updateMessagingSetting(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Messaging setting ID", example = "c9383d10-6fb5-4fc1-bd0a-50bf5a24d5b7") @ValidUuid @PathVariable final String id,
		@Valid @NotNull @RequestBody final MessagingSettingsRequest request) {

		return ok(messagingSettingsService.updateMessagingSetting(municipalityId, id, request));
	}

	@DeleteMapping(path = "/{id}", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Delete messaging setting", description = "Delete a messaging setting by ID", responses = {
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteMessagingSetting(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Messaging setting ID", example = "c9383d10-6fb5-4fc1-bd0a-50bf5a24d5b7") @ValidUuid @PathVariable final String id) {

		messagingSettingsService.deleteMessagingSetting(municipalityId, id);
		return noContent().build();
	}

	@DeleteMapping(path = "/{id}/key/{key}", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Delete messaging setting key", description = "Delete a specific key from a messaging setting", responses = {
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteMessagingSettingKey(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Messaging setting ID", example = "c9383d10-6fb5-4fc1-bd0a-50bf5a24d5b7") @ValidUuid @PathVariable final String id,
		@Parameter(name = "key", description = "Key to delete", example = "department_name") @NotNull @PathVariable final String key) {

		messagingSettingsService.deleteMessagingSettingKey(municipalityId, id, key);
		return noContent().build();
	}
}
