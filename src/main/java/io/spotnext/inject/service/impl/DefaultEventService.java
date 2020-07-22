package io.spotnext.inject.service.impl;

import java.util.List;

import io.spotnext.inject.Event;
import io.spotnext.inject.EventMethodHandler;
import io.spotnext.inject.annotations.Inject;
import io.spotnext.inject.annotations.Service;
import io.spotnext.inject.service.EventService;

@Service
public class DefaultEventService implements EventService {

	@Inject
	private List<EventMethodHandler> handlers;

	@Override
	public <E extends Event> void publishEvent(E event) {
		for (var handler : handlers) {
			handler.handleEvent(event);
		}
	}

	@Override
	public <E extends Event> void multicastEvent(E event) {
		for (var handler : handlers) {
			handler.handleEvent(event);
		}
	}

}
