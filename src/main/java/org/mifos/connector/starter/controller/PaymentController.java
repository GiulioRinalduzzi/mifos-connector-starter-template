/*
 * Copyright 2026 The Mifos Initiative.
 * Licensed under the Mozilla Public License 2.0.
 */

package org.mifos.connector.starter.controller;

import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that accepts JSON payment payloads and routes them through
 * the Camel payment pipeline (validation + processing).
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final ProducerTemplate producerTemplate;

	public PaymentController(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@PostMapping
	public ResponseEntity<String> processPayment(@RequestBody String jsonPayload) {
		Object result = producerTemplate.requestBody("direct:payment", jsonPayload);
		return ResponseEntity.ok(result != null ? result.toString() : "Payment Processed");
	}
}
