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

	@Schema(description = "Organization number of the organization connected to the information", examples = "162021005489")
	private String organizationNumber;

	@Schema(description = "Municipality ID", examples = "2281")
	private String municipalityId;

	@Schema(description = "Department name", examples = "SKM")
	private String departmentName;

	@Schema(description = "Method of delivery", examples = "EMAIL")
	private SnailMailMethod snailMailMethod;

	@Schema(description = "Indicates if sms is enabled for the given department", examples = "true")
	private Boolean smsEnabled;

	@Schema(description = "Indicates if rek is enabled for the given department", examples = "true")
	private Boolean rekEnabled;
}
