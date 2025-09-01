package se.sundsvall.messagingsettings.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.messagingsettings.integration.db.entity.MessagingSettingsEntity;

public interface MessagingSettingsSpecification {

	SpecificationBuilder<MessagingSettingsEntity> BUILDER = new SpecificationBuilder<>();

	static Specification<MessagingSettingsEntity> distinct() {
		return BUILDER.distinct();
	}

	static Specification<MessagingSettingsEntity> withMunicipalityId(final String municipalityId) {
		return BUILDER.buildMunicipalityIdFilter(municipalityId);
	}

	static Specification<MessagingSettingsEntity> withDepartmentId(final String departmentId) {
		return BUILDER.buildDepartmentIdFilter(departmentId);
	}

	static Specification<MessagingSettingsEntity> withDepartmentName(final String departmentName) {
		return BUILDER.buildDepartmentNameFilter(departmentName);
	}

	static Specification<MessagingSettingsEntity> withNamespace(final String namespace) {
		return BUILDER.buildNamespaceFilter(namespace);
	}

}
