package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Messaging settings request model")
public class MessagingSettingsRequest {

	@ArraySchema(arraySchema = @Schema(implementation = MessagingSettingValueRequest.class, description = "Values for the messaging setting"))
	@NotEmpty
	private List<@Valid MessagingSettingValueRequest> values;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Builder(setterPrefix = "with")
	@Schema(description = "Messaging setting value request model")
	public static class MessagingSettingValueRequest {

		@Schema(description = "Identifier key for the value setting", examples = "department_name", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank
		private String key;

		@Schema(description = "Stored value for the value setting", examples = "Department 44", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank
		private String value;

		@Schema(description = "Type of data for the stored value. Can be one of [STRING|NUMERIC|BOOLEAN]", examples = "STRING", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank
		private String type;
	}
}
