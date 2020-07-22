package io.spotnext.inject;

/**
 * Interface for the implementation of the generated event handlers
 */
public abstract class AbstractEventMethodHandler<T extends Event> implements EventMethodHandler<T> {

	private final Class<T> eventType;

	public AbstractEventMethodHandler() {
		this((Class<T>) Event.class);
	}
	
	protected AbstractEventMethodHandler(Class<T> eventType) {
		this.eventType = eventType;
	}

	@Override
	public abstract void handleEvent(T event) throws RuntimeException;

	boolean handlesEventType(Class<T> type) {
		return eventType.equals(type) || type.isAssignableFrom(eventType);
	}
}
