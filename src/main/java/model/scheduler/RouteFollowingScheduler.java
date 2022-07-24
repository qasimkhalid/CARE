package model.scheduler;

import model.PersonMovementInformation;

import java.util.ArrayList;
import java.util.List;

public class RouteFollowingScheduler {
    private List<PersonMovementInformation> personsFollowingRoutes = new ArrayList<>();

    public RouteFollowingScheduler() {
    }

    public List<PersonMovementInformation> getPersonsFollowingRoutes() {
        return personsFollowingRoutes;
    }

    public void setPersonsFollowingRoutes(List<PersonMovementInformation> personsFollowingRoutes) {
        this.personsFollowingRoutes = personsFollowingRoutes;
    }

    public void addPerson( PersonMovementInformation person ) {
        this.personsFollowingRoutes.add(person);
    }

    public void removeMultiplePersons(List<PersonMovementInformation> list){
        for(PersonMovementInformation l :list){
            this.personsFollowingRoutes.remove(l);
        }
    }

    public void updateScheduler( long deltaTime) {

    }

}
