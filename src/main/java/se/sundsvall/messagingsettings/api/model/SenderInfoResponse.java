package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "SenderInfo response")
public class SenderInfoResponse {

	@Schema(description = "Organization number of the organization connected to the information", examples = "162021005489")
	private String organizationNumber;

	@Schema(description = "Descriptive support text", examples = "Kontakta oss via epost eller telefon")
	private String supportText;

	@Schema(description = "Contact information URL", examples = "https://sundsvall.se/")
	private String contactInformationUrl;

	@Schema(description = "Contact information phone number", examples = "060-19 10 00")
	private String contactInformationPhoneNumber;

	@Schema(description = "Contact information e-mail address", examples = "sundsvalls.kommun@sundsvall.se")
	private String contactInformationEmail;

	@Schema(description = "Name of contact information e-mail sender", examples = "Sundsvalls kommun")
	private String contactInformationEmailName;

	@Schema(description = "Name of SMS sender", examples = "Sundsvall")
	private String smsSender;

	@Schema(description = "Folder name", examples = "Foo")
	private String folderName;
}
