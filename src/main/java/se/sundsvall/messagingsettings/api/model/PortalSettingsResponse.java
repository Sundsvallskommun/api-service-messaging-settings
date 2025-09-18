package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.messagingsettings.enums.SnailMailMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "PortalSettings response")
public class PortalSettingsResponse {

	@Schema(description = "Organization number of the organization connected to the information", example = "162021005489")
	private String organizationNumber;

	@Schema(description = "Municipality ID", example = "2281")
	private String municipalityId;

	@Schema(description = "Department name", example = "SKM")
	private String departmentName;

	@Schema(description = "Method of delivery", example = "EMAIL")
	private SnailMailMethod snailMailMethod;

	@Schema(description = "Indicates if sms is enabled for the given department", example = "true")
	private Boolean smsEnabled;

	@Schema(description = "Indicates if rek is enabled for the given department", example = "true")
	private Boolean rekEnabled;
}
