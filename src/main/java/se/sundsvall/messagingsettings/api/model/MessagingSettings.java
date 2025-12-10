package se.sundsvall.messagingsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Messaging settings response model")
public class MessagingSettings {

	@Schema(description = "Id for the messaging setting instance", examples = "c9383d10-6fb5-4fc1-bd0a-50bf5a24d5b7", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "Municipality id for municipality that the messaging setting instance belongs to", examples = "2281", accessMode = READ_ONLY)
	private String municipalityId;

	@Schema(description = "Timestamp when the instance was created", examples = "2025-10-24T15:30:00+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Timestamp when the instance was last updated. Null if instance has never been updated.", examples = "2025-10-25T16:30:00+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime updated;

	@ArraySchema(arraySchema = @Schema(implementation = MessagingSettingValue.class, description = "Values connected to the messaging setting instance", accessMode = READ_ONLY))
	private List<MessagingSettingValue> values;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Builder(setterPrefix = "with")
	@Schema(description = "Messaging setting value model")
	public static class MessagingSettingValue {

		@Schema(description = "Identifier key for the value setting", examples = "department_name", accessMode = READ_ONLY)
		private String key;

		@Schema(description = "Stored value for the value setting", examples = "Department 44", accessMode = READ_ONLY)
		private String value;

		@Schema(description = "Type of data for the stored value. Can be one of [STRING|NUMERIC|BOOLEAN]", examples = "STRING", accessMode = READ_ONLY)
		private String type;
	}
}
