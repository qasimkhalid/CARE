package model;

import java.util.List;

public class PersonController {

    private final String person;
    private final String personId;

    private PathTraversal pathTraversal;

    public PersonController(String person, String personId) {
        this.person = person;
        this.personId = personId;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPerson() {
        return person;
    }

    public void assignRoute(List<String> routeAssigned) {
        this.pathTraversal = new PathTraversal(routeAssigned) {
            @Override
            public void onEdgeTraversed(String destination) {
                // update destination of this person with personId -> destination
                // which is current location
            }

            @Override
            public void onLeavingLastDestination() {
                // clear location of this person
            }

            @Override
            public void onPathTraversed() {
                // update destination of person with personId -> as he
                // has completed the path
            }
        };
    }

    public boolean isResting()
    {
        return this.pathTraversal.isResting;
    }

    public void Update(long deltaTime) {
        if (this.pathTraversal == null)
            return;

        this.pathTraversal.move(deltaTime);
    }
}
