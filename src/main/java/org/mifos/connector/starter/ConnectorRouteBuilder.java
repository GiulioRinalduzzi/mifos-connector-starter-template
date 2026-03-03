/*
 * Copyright 2026 The Mifos Initiative.
 * Licensed under the Mozilla Public License 2.0.
 */

package org.mifos.connector.starter;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.mifos.connector.starter.dto.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route builder that processes JSON payment input.
 *
 * <p>
 * This route:
 * <ul>
 * <li>Receives JSON input via a direct endpoint</li>
 * <li>Unmarshals to {@link PaymentRequest} using Jackson</li>
 * <li>Validates using Jakarta Bean Validation (via Camel's bean-validator)</li>
 * <li>Logs {@code "Payment Processed"} on success</li>
 * </ul>
 *
 * <p>
 * In a production connector, this would integrate with Payment Hub EE (PH-EE)
 * orchestration (e.g. Zeebe) and external AMS/channel systems.
 */
@Component
public class ConnectorRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorRouteBuilder.class);

	@Override
	public void configure() {
		JacksonDataFormat paymentFormat = new JacksonDataFormat(PaymentRequest.class);

		from("direct:payment").routeId("connector-payment-route").unmarshal(paymentFormat)
				.to("bean-validator:defaultValidator").process(exchange -> {
					PaymentRequest req = exchange.getIn().getBody(PaymentRequest.class);
					LOG.info("Payment Processed: transactionId={}, accountId={}, amount={}", req.getTransactionId(),
							req.getAccountId(), req.getAmount());
				}).setBody(constant("Payment Processed")).log(LoggingLevel.INFO, "Payment Processed");
	}
}
