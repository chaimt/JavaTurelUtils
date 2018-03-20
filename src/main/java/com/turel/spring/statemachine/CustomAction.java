package com.turel.spring.statemachine;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

public class CustomAction<E> {

	protected <E> Message<E> clone(Message<E> message, E event) {
		return clone(message, event, null);
	}

	protected <E> Message<E> clone(Message<E> message, E event, Map<String, Object> header) {
		final MessageBuilder<E> sfmEventsMessageBuilder = MessageBuilder.withPayload(event);
		final MessageHeaders headers = message.getHeaders();
		if (header != null) {
			header.putAll(headers);
			sfmEventsMessageBuilder.copyHeaders(header);
		} else {
			sfmEventsMessageBuilder.copyHeaders(headers);
		}
		return sfmEventsMessageBuilder
				.build();

	}
}
