package se.sundsvall.messagingsettings.integration.db.mapper;

import java.util.List;
import java.util.Objects;
import se.sundsvall.messagingsettings.api.model.MessagingSettings;
import se.sundsvall.messagingsettings.api.model.MessagingSettings.MessagingSettingValue;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest;
import se.sundsvall.messagingsettings.api.model.MessagingSettingsRequest.MessagingSettingValueRequest;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingEntity;
import se.sundsvall.messagingsettings.integration.db.model.MessagingSettingValueEmbeddable;
import se.sundsvall.messagingsettings.integration.db.model.enums.ValueType;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public final class EntityMapper {

	private EntityMapper() {}

	public static MessagingSettings toMessagingSettings(final MessagingSettingEntity nullableSettingEntity) {
		return ofNullable(nullableSettingEntity)
			.map(settingEntity -> MessagingSettings.builder()
				.withId(settingEntity.getId())
				.withMunicipalityId(settingEntity.getMunicipalityId())
				.withValues(toMessagingSettingValues(settingEntity.getValues()))
				.withCreated(settingEntity.getCreated())
				.withUpdated(settingEntity.getUpdated())
				.build())
			.orElse(null);
	}

	private static List<MessagingSettingValue> toMessagingSettingValues(final List<MessagingSettingValueEmbeddable> nullableSettingValueEmbeddables) {
		return ofNullable(nullableSettingValueEmbeddables).orElse(emptyList()).stream()
			.map(EntityMapper::toMessagingSettingValue)
			.filter(Objects::nonNull)
			.toList();
	}

	private static MessagingSettingValue toMessagingSettingValue(final MessagingSettingValueEmbeddable nullableSettingValueEmbeddable) {
		return ofNullable(nullableSettingValueEmbeddable)
			.map(settingValueEmbeddable -> MessagingSettingValue.builder()
				.withKey(settingValueEmbeddable.getKey())
				.withType(settingValueEmbeddable.getType().name())
				.withValue(settingValueEmbeddable.getValue())
				.build())
			.orElse(null);
	}

	/**
	 * Maps create request to entity
	 *
	 * @param  municipalityId the municipality id
	 * @param  request        the request to map
	 * @return                the mapped entity
	 */
	public static MessagingSettingEntity toEntity(final String municipalityId, final MessagingSettingsRequest request) {
		return ofNullable(request)
			.map(req -> MessagingSettingEntity.builder()
				.withMunicipalityId(municipalityId)
				.withValues(toEmbeddableValues(req.getValues()))
				.build())
			.orElse(null);
	}

	/**
	 * Updates entity values from the request if both exist. If the entity or request is null, the entity is returned
	 * unchanged. This method performs a partial update (PATCH semantics): - Existing values not in the request are
	 * preserved - Values in the
	 * request update existing values (matched by a key) - New values in the request are added
	 *
	 * @param  entity  the entity to update
	 * @param  request the request to update from
	 * @return         the updated entity
	 */
	public static MessagingSettingEntity updateEntity(final MessagingSettingEntity entity, final MessagingSettingsRequest request) {
		if (entity != null && request != null) {
			entity.setValues(mergeValues(entity.getValues(), toEmbeddableValues(request.getValues())));
		}
		return entity;
	}

	/**
	 * Merges existing values with new values from the request. For values with matching keys, the new value replaces the
	 * existing one. Values not in the new list are preserved.
	 *
	 * @param  existingValues the current values
	 * @param  newValues      the values from the update request
	 * @return                the merged values list
	 */
	private static List<MessagingSettingValueEmbeddable> mergeValues(
		final List<MessagingSettingValueEmbeddable> existingValues,
		final List<MessagingSettingValueEmbeddable> newValues) {

		final var existing = ofNullable(existingValues).orElse(emptyList());
		final var updates = ofNullable(newValues).orElse(emptyList());

		// Start with existing values, replacing any that are in the update
		final var merged = existing.stream()
			.filter(existingValue -> updates.stream()
				.noneMatch(newValue -> newValue.getKey().equals(existingValue.getKey())))
			.collect(toList());

		// Add all values from the update (both new and updated)
		merged.addAll(updates);

		return merged;
	}

	/**
	 * Maps request values to embeddable values
	 *
	 * @param  values the values to map
	 * @return        the mapped values
	 */
	private static List<MessagingSettingValueEmbeddable> toEmbeddableValues(final List<MessagingSettingValueRequest> values) {
		return ofNullable(values).orElse(emptyList()).stream()
			.map(value -> MessagingSettingValueEmbeddable.builder()
				.withKey(value.getKey())
				.withValue(value.getValue())
				.withType(ValueType.valueOf(value.getType()))
				.build())
			.toList();
	}

}
