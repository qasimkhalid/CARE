package helper;

public abstract class TimeStepListener {
    public abstract void onTimeStep(long timeStep);

    public void onPostTimeStep(long timeStep) {

    }
}