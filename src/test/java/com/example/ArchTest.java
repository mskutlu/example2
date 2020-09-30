package com.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.HibernateConfig;
import io.hypersistence.optimizer.core.config.JpaConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example");

        noClasses()
            .that()
                .resideInAnyPackage("com.example.service..")
            .or()
                .resideInAnyPackage("com.example.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.example.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }

    @Bean
    public HypersistenceOptimizer hypersistenceOptimizer(
        SessionFactory sessionFactory) {
        return new HypersistenceOptimizer(
            new HibernateConfig(sessionFactory)
        );
    }
}
