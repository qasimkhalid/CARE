package model.scheduler;

import helper.CommonOperations;
import model.PersonController;
import model.PersonMovementInformation;

import java.util.ArrayList;
import java.util.List;

public class MovementScheduler {
    private final List<PersonController> movingPersons = new ArrayList<>();

    public MovementScheduler() {
    }

    public List<PersonController> getMovingPersons() {
        return movingPersons;
    }

    public void addMovingPerson( PersonController person ) {
        this.movingPersons.add(person);
    }

    public void removeMovingPersons(List<PersonController> list){
        for(PersonController l :list){
            this.movingPersons.remove(l);
        }
    }

    public List<PersonController> updatePersonMovement(long deltaTime, List<PersonController> list){

        for (PersonController p: this.movingPersons) {
            p.Update(deltaTime);
        }

        return personWhoFinishedMovement;
    }
}
