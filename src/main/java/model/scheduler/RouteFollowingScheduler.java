package model.scheduler;

import model.Person;
import model.PersonController;
import model.PersonMovementInformation;

import java.util.ArrayList;
import java.util.List;

public class RouteFollowingScheduler {
    private List<PersonController> personsFollowingRoutes = new ArrayList<>();

    public RouteFollowingScheduler() {
    }

    public List<PersonController> getPersonsFollowingRoutes() {
        return personsFollowingRoutes;
    }

    public void setPersonsFollowingRoutes(List<PersonController> personsFollowingRoutes) {
        this.personsFollowingRoutes = personsFollowingRoutes;
    }

    public void addPerson(PersonController person) {
        this.personsFollowingRoutes.add(person);
    }

    public void removeMultiplePersons(List<PersonController> list) {
        for (PersonController l : list) {
            this.personsFollowingRoutes.remove(l);
        }
    }

    public void updateScheduler(long deltaTime) {

    }

}
