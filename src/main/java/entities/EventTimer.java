package entities;

import java.util.ArrayList;
import java.util.List;

public class EventTimer {
    private static EventTimer instance;
    private final List<ITimeStepListener> listeners = new ArrayList<>();
    private List<ITimeStepListener> newListeners = new ArrayList<>();
    private List<ITimeStepListener> listenersToRemove = new ArrayList<>();

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

    public void addTimeStepListener(ITimeStepListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void removeTimeStepListener(ITimeStepListener listener) {
        if (!listeners.contains(listener)) {
            return;
        }

        listenersToRemove.add(listener);
    }

    public void updateTimeStepListener(ITimeStepListener listener) {
        if (!listeners.contains(listener)) {
            return;
        }
        newListeners.add(listener);
    }

    public void doTimeStep(long timeStep) {
        //System.out.println("..do time step.." + timeStep);

        for (ITimeStepListener listener : listeners) {
            listener.onTimeStep(timeStep);
        }

        listeners.removeAll(listenersToRemove);
        listenersToRemove = new ArrayList<>();

        for (ITimeStepListener listener : newListeners){
            if(!listeners.contains(listener)){
                listeners.add(listener);
            }
        }
        newListeners = new ArrayList<>();
    }
}