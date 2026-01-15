package se.sundsvall.messagingsettings.integration.db;

import static java.nio.file.Files.readString;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("junit")
class SchemaVerificationTest {

	private static final String STORED_SCHEMA_FILE = "db/schema.sql";

	@Value("${spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target}")
	private String generatedSchemaFile;

	@Test
	void verifySchemaUpdates() throws IOException, URISyntaxException {
		final var storedSchema = getResourceString();
		final var generatedSchema = Files.readString(Path.of(generatedSchemaFile));

		assertThat(generatedSchema)
			.as("Please reflect modifications to entities in file: %s".formatted(STORED_SCHEMA_FILE))
			.isEqualToNormalizingWhitespace(storedSchema);
	}

	private String getResourceString() throws IOException, URISyntaxException {
		return readString(get(Objects.requireNonNull(getClass().getClassLoader().getResource(STORED_SCHEMA_FILE)).toURI()));
	}
}
