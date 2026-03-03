/*
 * Copyright 2026 The Mifos Initiative.
 * Licensed under the Mozilla Public License 2.0.
 */

package org.mifos.connector.starter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO representing a payment request payload.
 *
 * <p>
 * Uses Jakarta Bean Validation ({@code jakarta.validation}) for input
 * validation. Tip: don't use {@code javax} anymore; Spring Boot 3 needs
 * {@code jakarta}.
 */
public class PaymentRequest {

	@NotBlank(message = "Transaction ID is required")
	private String transactionId;

	@NotBlank(message = "Account ID is required")
	private String accountId;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	private BigDecimal amount;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
