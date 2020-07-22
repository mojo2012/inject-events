package io.spotnext.inject;

import io.spotnext.inject.annotations.EventHandler;
import io.spotnext.inject.annotations.Service;
import io.spotnext.support.util.Loggable;

@Service
public class CustomEventListener implements Loggable {

	private Runnable eventCallback;

	@EventHandler(StartupEvent.class)
	public void onStartup(StartupEvent event) {
		log().info("Event fired");
		eventCallback.run();
	}

	public Runnable getEventCallback() {
		return eventCallback;
	}

	public void setEventCallback(Runnable eventCallback) {
		this.eventCallback = eventCallback;
	}

	public static class StartupEvent implements Event {
		//
	}
}