package wg.event;

public class Event {
	
	public String eventType;
	
	public Object eventData;

	public Event(String eventType, Object eventData) {
		this.eventType = eventType;
		this.eventData = eventData;
	}

	@Override
	public String toString() {
		return "Event{" +
				"eventType='" + eventType + '\'' +
				", eventData=" + eventData +
				'}';
	}
}
