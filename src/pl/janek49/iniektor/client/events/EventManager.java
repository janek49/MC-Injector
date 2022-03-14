package pl.janek49.iniektor.client.events;

import pl.janek49.iniektor.client.modules.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
    public HashMap<Class<? extends IEvent>, List<EventHandler>> RegisteredEventHandlers;

    public EventManager() {
        RegisteredEventHandlers = new HashMap<>();
    }

    public void registerHandler(Class<? extends IEvent> ie, EventHandler eh) {
        if (RegisteredEventHandlers.containsKey(ie)) {
            RegisteredEventHandlers.get(ie).add(eh);
        } else {
            List<EventHandler> leh = new ArrayList<>();
            leh.add(eh);
            RegisteredEventHandlers.put(ie, leh);
        }
    }

    public void unregisterHandler(Class<? extends IEvent> ie, EventHandler eh) {
        if (RegisteredEventHandlers.containsKey(ie)) {
            RegisteredEventHandlers.get(ie).remove(eh);
        }
    }

    public void fireEvent(IEvent ie) {
        if (RegisteredEventHandlers.containsKey(ie.getClass())) {
            for (EventHandler eh : RegisteredEventHandlers.get(ie.getClass())) {
                if (eh instanceof Module && !((Module) eh).isEnabled)
                    continue;
                eh.onEvent(ie);
            }
        }
    }
}
