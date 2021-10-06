package model;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<PersonMovementTime> movingPersons = new ArrayList<>();

    public Scheduler() {

    }

    public List<PersonMovementTime> getMovingPersons() {
        return movingPersons;
    }

    public void setMovingPersons( List<PersonMovementTime> movingPersons ) {
        this.movingPersons = movingPersons;
    }

    public void addMovingPerson(String person, long timeRequired, long timeElapsed, String origin, String destination){
        PersonMovementTime m = new PersonMovementTime(person, timeRequired, timeElapsed, origin, destination);
        movingPersons.add(m);
    }

    public void removeMovingPerson(List<PersonMovementTime> list){
        for(PersonMovementTime l :list){
            movingPersons.remove(l);
        }
    }

    public void  updateMovingPerson(){
    }


    public void deleteMovingPerson(){
    }

    public List<PersonMovementTime> update( long deltaTime, List<PersonMovementTime> list){
        List<PersonMovementTime> personWhoFinishedMovement = new ArrayList<>();
        for(PersonMovementTime l : list){
            long getPersonTimeElapsed = l.getTimeElapsed();
            long getPersonTimeRequired = l.getTimeRequired();
            if (getPersonTimeRequired > getPersonTimeElapsed){
                l.setTimeElapsed(getPersonTimeElapsed + deltaTime);
            } else {
                personWhoFinishedMovement.add(l);
            }
        }
        removeMovingPerson(personWhoFinishedMovement);
        return personWhoFinishedMovement;
    }



}
