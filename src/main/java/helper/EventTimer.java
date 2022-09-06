package helper;

import java.util.ArrayList;
import java.util.List;

public class EventTimer {
    private static EventTimer instance;
    private List<TimeStepListener> listeners = new ArrayList<>();

    /**
     * Restrict it from creating an instance outside this class scope
     */
    private EventTimer() {
    }

    public final static EventTimer Instance() {
        if (instance == null) {
            instance = new EventTimer();
        }

        return instance;
    }

    public void addTimeStepListener(TimeStepListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
    }

    public void removeTimeStepListener(TimeStepListener listener) {
        if (listeners.contains(listener) == false) {
            return;
        }

        listeners.remove(listener);
    }

    public void doTimeStep(long timestep) {
        listeners.forEach(t -> t.onTimeStep(timestep));
    }

    public void doPostTimeStep(long timestep) {
        listeners.forEach(t -> t.onPostTimeStep(timestep));
    }
}