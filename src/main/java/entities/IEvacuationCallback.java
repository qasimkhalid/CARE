package entities;

public interface IEvacuationCallback {
    /**
     * This method checks how many persons should complete the evacuation.
     * @param personController - personController Object
     */
    void evacuationEnded(PersonController personController);
}
