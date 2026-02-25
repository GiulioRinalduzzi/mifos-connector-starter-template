/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fineract.cn.connector.starter;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.fineract.cn.connector.starter.dto.PaymentRequest;
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
 * <li>Unmarshals to PaymentRequest using Jackson</li>
 * <li>Validates using Jakarta Bean Validation (via Camel's validation)</li>
 * <li>Logs "Payment Processed" on success</li>
 * </ul>
 *
 * <p>
 * In a production connector, this would integrate with Payment Hub EE
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
