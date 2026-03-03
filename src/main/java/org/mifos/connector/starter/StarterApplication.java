/*
 * Copyright 2026 The Mifos Initiative.
 * Licensed under the Mozilla Public License 2.0.
 */

package org.mifos.connector.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the PH-EE Connector Starter application.
 *
 * <p>
 * This Spring Boot application provides a reference template for building
 * Payment Hub EE (PH-EE) connectors with Apache Camel routes, Jakarta
 * validation, and Mifos coding standards.
 */
@SpringBootApplication
public class StarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarterApplication.class, args);
	}
}
