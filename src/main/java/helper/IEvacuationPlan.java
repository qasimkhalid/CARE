package helper;

public abstract class IEvacuationPlan {
    // Calls only once
    abstract void Execute();

    // Calls every time step
    abstract void Update(long timeStep);

    // returns true if this plan is still in progress
    public boolean inProgress() {
        // a query that returns all the persons located in the building other then exits
        /* e.g. return BuildingUtils.hasAnyPerson(); */
        return false;
    }

    // it should be abstract
    public void Interrupt(/* List<Space> spacesWithInterrupts */) {
        // may be it has some parameters or information from your ontology that has some
        // resonable data
        // based on that data you can regenerate the paths for evacuating persons
    }
}

class BFSPlan extends IEvacuationPlan {

    @Override
    public void Execute() {
        // TODO Auto-generated method stub
        // get all exits
        // find persons's locations using BFS from exits
        // it will return us paths from person to exit
        // start moving the person
    }

    @Override
    public void Update(long timeStep) {
        // TODO Auto-generated method stub
        // here check in update if a person has moved to another room, update its
        // location to new room
    }

    @Override
    public void Interrupt() {
        // if interrupt happens find people who are using that space and do the
        // following again
        // find affected persons's locations using BFS from exits
        // it will return us paths from person to exit
        // start moving the person
    }
}

class DFSPlan extends IEvacuationPlan {

    @Override
    public void Execute() {
        // TODO Auto-generated method stub
    }

    @Override
    public void Update(long timeStep) {
        // TODO Auto-generated method stub

    }

}

class AStarPlan extends IEvacuationPlan {

    @Override
    public void Execute() {
        // TODO Auto-generated method stub
    }

    @Override
    public void Update(long timeStep) {
        // TODO Auto-generated method stub

    }
}