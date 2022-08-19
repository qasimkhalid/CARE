package model;

import java.util.List;

import helper.AutomatedOperations;

public class PersonController {

    private final Person person;
    private PathTraversal pathTraversal;
    private boolean isPathInterrupt = false;

    public PersonController(Person person) {
        this.person = person;
    }

    public String getPersonId() {
        return this.person.getId();
    }

    public String getName() {
        return this.person.getName();
    }

    public boolean IsPathInterrupt() {
        return this.isPathInterrupt;
    }

    public void assignRoute(List<String> routeAssigned) {
        this.pathTraversal = new PathTraversal(routeAssigned) {
            @Override
            public void onEdgeTraversed(String newLocation) {
                // update destination of this person with new destination where he has landed!
                person.setLocation(newLocation);
                AutomatedOperations.updateModelWhenPersonTraverseSingleEdge(getName(), newLocation);
            }

            @Override
            public void onPathComplete() {
                // update destination of person with personId -> as he
                // has completed the path
                AutomatedOperations.updateModelWhenPersonCompletesPath(getName());
            }

            @Override
            public void onPathInterrupt() {
                // here you should do any operations to your model using AutomatedOperations
                // after operation we will set isPathInterrupt to true, which will be consumed
                // by respective schedular to decide another path
                isPathInterrupt = true;
            }
        };
    }

    public boolean isResting() {
        return this.pathTraversal.isResting;
    }

    public void Update(long deltaTime) {
        updateMovementPhase(deltaTime);
        updateRestingPhase(deltaTime);
    }

    private void updateMovementPhase(long deltaTime) {
        if (this.pathTraversal == null)
            return;

        this.pathTraversal.move(deltaTime);
    }

    private void updateRestingPhase(long deltaTime) {
        if (this.isResting() == false)
            return;

        // Check logic for resting phase, if his rest is over,
        // set resting state to some other state. and isResting should be false

        // not part of person controller, just discussion
        // MainAbbu
        // Abbu ne bachay ko order dia k rest kr yan nikal ja
        // ab bachay k zimay yehi do kaam haen, agar bachay se in
        // me se koi b kam nahi hua kisi na-gahani afat ki waja se
        // to wo abu ko btae ga, k abu g i am interrupted!!!
        // ab abu janay k wo is bachay ka kya krtay haen yan is se
        // kya kaam lete haen
    }
}
