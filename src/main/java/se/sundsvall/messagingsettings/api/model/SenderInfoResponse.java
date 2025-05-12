package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
@Schema(description = "SenderInfo response")
public class SenderInfoResponse {

	@Schema(description = "Descriptive support text", example = "Kontakta oss via epost eller telefon")
	private String supportText;

	@Schema(description = "Contact information URL", example = "https://sundsvall.se/")
	private String contactInformationUrl;

	@Schema(description = "Contact information phone number", example = "4660191000")
	private String contactInformationPhoneNumber;

	@Schema(description = "Contact information e-mail address", example = "sundsvalls.kommun@sundsvall.se")
	private String contactInformationEmail;

	@Schema(description = "Name of SMS sender", example = "Sundsvall")
	private String smsSender;
}
