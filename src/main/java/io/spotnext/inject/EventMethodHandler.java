package io.spotnext.inject;

public interface EventMethodHandler<T extends Event> {

	void handleEvent(T event) throws RuntimeException;


}
