/*
 * Copyright 2026 The Mifos Initiative.
 * Licensed under the Mozilla Public License 2.0.
 */

package org.mifos.connector.starter.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sample Zeebe worker for PH-EE connectors. Mirrors the worker pattern used in
 * reference connectors (e.g. ph-ee-connector-mccbs): {@code @JobWorker} with
 * {@code @Variable} parameters, returns a {@code Map} of variables for the
 * workflow.
 */
@Component
public class StarterZeebeWorker {

	private static final Logger LOG = LoggerFactory.getLogger(StarterZeebeWorker.class);

	/**
	 * Worker for job type {@code starter-worker}. Validates that the {@code amount}
	 * variable is positive and returns workflow variables accordingly.
	 *
	 * @param transactionId
	 *            workflow transaction id (optional)
	 * @param amount
	 *            payment amount (Number or String), may be null
	 * @return map of variables: {@code amountValid}, and on error
	 *         {@code errorCode}, {@code errorMessage}
	 */
	@JobWorker(type = "starter-worker")
	public Map<String, Object> handleStarterWorker(@Variable(name = "transactionId") String transactionId,
			@Variable(name = "amount") Object amount) {

		LOG.info("[Starter] starter-worker received transactionId={}, amount={}", transactionId, amount);

		Map<String, Object> variables = new HashMap<>();
		boolean amountValid = isAmountPositive(amount);

		variables.put("amountValid", amountValid);
		if (!amountValid && amount != null) {
			variables.put("errorCode", "INVALID_AMOUNT");
			variables.put("errorMessage", "Amount must be a positive number; received: " + amount);
		}

		LOG.info("[Starter] starter-worker completed transactionId={}, amountValid={}", transactionId, amountValid);
		return variables;
	}

	private boolean isAmountPositive(Object amountObject) {
		if (amountObject == null) {
			return false;
		}
		if (amountObject instanceof Number number) {
			return new BigDecimal(number.toString()).compareTo(BigDecimal.ZERO) > 0;
		}
		if (amountObject instanceof String text) {
			try {
				return new BigDecimal(text).compareTo(BigDecimal.ZERO) > 0;
			} catch (NumberFormatException ex) {
				LOG.warn("[Starter] starter-worker received non-numeric amount value: {}", text);
				return false;
			}
		}
		LOG.warn("[Starter] starter-worker received unsupported amount type: {} ({})", amountObject,
				amountObject.getClass());
		return false;
	}
}
