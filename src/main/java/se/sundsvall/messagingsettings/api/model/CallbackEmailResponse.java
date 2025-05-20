package se.sundsvall.messagingsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
@Schema(description = "CallbackEmail response")
public class CallbackEmailResponse {

	@Schema(description = "Callback e-mail address", example = "no-reply@domain.tld")
	private String callbackEmail;
}
