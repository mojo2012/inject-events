package io.spotnext.inject.service;

import io.spotnext.inject.Event;

public interface EventService {

	/**
	 * Synchronously notify all <strong>matching</strong> listeners registered
	 * with this application of an application event. Events may be framework
	 * events (such as RequestHandledEvent) or application-specific events.
	 *
	 * @param event
	 *            the event to publish
	 * @see org.springframework.web.context.support.RequestHandledEvent
	 * @param <E> a E object.
	 */
	<E extends Event> void publishEvent(final E event);

	/**
	 * Multicast the given application event to appropriate listeners.
	 *
	 * @param event
	 *            the event to multicast
	 * @param <E> a E object.
	 */
	<E extends Event> void multicastEvent(E event);
}
