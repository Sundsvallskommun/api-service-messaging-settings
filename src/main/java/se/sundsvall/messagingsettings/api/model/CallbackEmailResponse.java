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
@Schema(description = "CallbackEmail response")
public class CallbackEmailResponse {

	@Schema(description = "Organization number of the organization connected to the information", examples = "162021005489")
	private String organizationNumber;

	@Schema(description = "Callback e-mail address", examples = "no-reply@domain.tld")
	private String callbackEmail;
}
