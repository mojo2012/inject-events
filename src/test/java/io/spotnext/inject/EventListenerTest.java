package io.spotnext.inject;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.spotnext.inject.CustomEventListener.StartupEvent;
import io.spotnext.inject.instrumentation.InjectionTransformer;
import io.spotnext.inject.service.EventService;
import io.spotnext.instrumentation.DynamicInstrumentationLoader;

public class EventListenerTest {

	static {
		// dynamically attach java agent to JVM if not already present and add the injection transformer for load-time injection
		DynamicInstrumentationLoader.initialize(InjectionTransformer.class);
	}

	@Test
	public void testEventHandler() {
		final var result = new Result();

		// setup event handler
		final var eventHandler = Context.instance().getBean(CustomEventListener.class);
		eventHandler.setEventCallback(() -> result.called = true);

		// get all event method handlers
		final var handlers = Context.instance().getBeans(EventMethodHandler.class);

		final var eventService = Context.instance().getBean(EventService.class);
		eventService.publishEvent(new StartupEvent());
		
		// assertions
		assertTrue(handlers != null && handlers.size() > 0);
		assertTrue(result.called);
	}

	private static class Result {
		boolean called;
	}
}