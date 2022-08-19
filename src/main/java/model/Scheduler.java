package model;

import com.hp.hpl.jena.rdf.model.Resource;
import helper.HelpingVariables;
import helper.MathOperations;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<PersonMovementInformation> movingPersons = new ArrayList<>();
    private List<PersonMovementInformation> restingPersons = new ArrayList<>();

    public Scheduler() {
    }

    public List<PersonMovementInformation> getMovingPersons() {
        return movingPersons;
    }

    public void setMovingPersons( List<PersonMovementInformation> movingPersons ) {
        this.movingPersons = movingPersons;
    }

    public List<PersonMovementInformation> getRestingPersons() {
        return restingPersons;
    }

    public void setRestingPersons( List<PersonMovementInformation> restingPersons ) {
        this.restingPersons = restingPersons;
    }

    public void addMovingPerson(String person, long timeRequired, long timeElapsed, String origin, String destination, String id){
        PersonMovementInformation m = new PersonMovementInformation(person, timeRequired, timeElapsed, origin, destination, id);
        this.movingPersons.add(m);
    }

    public void addMovingPerson( PersonMovementInformation person ) {
        this.movingPersons.add(person);
    }

    public void removeMovingPerson(List<PersonMovementInformation> list){
        for(PersonMovementInformation l :list){
            this.movingPersons.remove(l);
        }
    }

    public void addRestingPerson(String person){
        PersonMovementInformation m = new PersonMovementInformation(person, MathOperations.getRandomNumberInRange(10, 1) * 1000L, 0);
        this.restingPersons.add(m);
    }

    public void removeRestingPerson(List<PersonMovementInformation> list){
        Resource personInstance;
        for(PersonMovementInformation l :list){
            this.restingPersons.remove(l);
            personInstance = CareeInfModel.Instance().getResource(l.getPerson());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
        }
    }

    public List<PersonMovementInformation> updatePersonMovement(long deltaTime, List<PersonMovementInformation> list){
        List<PersonMovementInformation> personWhoFinishedMovement = processElapsedTime(deltaTime, list);
        removeMovingPerson(personWhoFinishedMovement);
        return personWhoFinishedMovement;
    }

    public void updatePersonResting(long deltaTime, List<PersonMovementInformation> list){
        List<PersonMovementInformation> personWhoFinishedResting = processElapsedTime(deltaTime, list);
        removeRestingPerson(personWhoFinishedResting);
    }


    private List<PersonMovementInformation> processElapsedTime(long deltaTime, List<PersonMovementInformation> list) {
        List<PersonMovementInformation> personWhoseTimeFinished = new ArrayList<>();
        long getPersonTimeElapsed;
        long getPersonTimeRequired;
        for(PersonMovementInformation pti : list){
            getPersonTimeElapsed = pti.getTimeElapsed();
            getPersonTimeRequired = pti.getTimeRequired();
            if (getPersonTimeRequired > getPersonTimeElapsed){
                pti.setTimeElapsed(getPersonTimeElapsed + deltaTime);
            } else {
                personWhoseTimeFinished.add(pti);
            }
        }
        return personWhoseTimeFinished;
    }

}
