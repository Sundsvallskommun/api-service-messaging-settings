package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import se.sundsvall.messagingsettings.integration.db.entity.enums.SnailMailMethod;

@Data
@Builder(setterPrefix = "with")
@Schema(description = "PortalSettings response")
public class PortalSettingsResponse {

	@Schema(description = "Municipality ID", example = "2281")
	private String municipalityId;

	@Schema(description = "Department name", example = "SKM")
	private String departmentName;

	@Schema(description = "Method of delivery", example = "EMAIL", allowableValues = {
		"SC_ADMIN", "EMAIL"
	})
	private SnailMailMethod snailMailMethod;
}
