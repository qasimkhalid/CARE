package helper;

import java.util.ArrayList;
import java.util.List;

public class EventTimer {
    private static EventTimer instance;
    private final List<TimeStepListener> listeners = new ArrayList<>();

    /**
     * Restrict it from creating an instance outside this class scope
     */
    private EventTimer() {
    }

    public static EventTimer Instance() {
        if (instance == null) {
            instance = new EventTimer();
        }

        return instance;
    }

    public void addTimeStepListener(TimeStepListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        try {
            listeners.add(listener);
        }
        catch (Exception e) {

        }
    }

    public void removeTimeStepListener(TimeStepListener listener) {
        if (!listeners.contains(listener)) {
            return;
        }

        listeners.remove(listener);
    }

    public void doTimeStep(long timeStep) {
        System.out.println("..do time step.." + timeStep);
        for (TimeStepListener listener : listeners) {
            listener.onTimeStep(timeStep);
        }
    }

    public void doPostTimeStep(long timeStep) {
        System.out.println("..do post time step.." + timeStep);
        for (TimeStepListener listener : listeners) {
            listener.onPostTimeStep(timeStep);
        }
    }
}