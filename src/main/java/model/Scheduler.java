package model;

import com.hp.hpl.jena.rdf.model.Resource;
import helper.HelpingVariables;
import helper.MathOperations;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<PersonTimerInformation> movingPersons = new ArrayList<>();
    private List<PersonTimerInformation> restingPersons = new ArrayList<>();

    public Scheduler() {
    }

    public List<PersonTimerInformation> getMovingPersons() {
        return movingPersons;
    }

    public void setMovingPersons( List<PersonTimerInformation> movingPersons ) {
        this.movingPersons = movingPersons;
    }

    public List<PersonTimerInformation> getRestingPersons() {
        return restingPersons;
    }

    public void setRestingPersons( List<PersonTimerInformation> restingPersons ) {
        this.restingPersons = restingPersons;
    }

    public void addMovingPerson(String person, long timeRequired, long timeElapsed, String origin, String destination, String id){
        PersonTimerInformation m = new PersonTimerInformation(person, timeRequired, timeElapsed, origin, destination, id);
        this.movingPersons.add(m);
    }

    public void addMovingPerson( PersonTimerInformation person ) {
        this.movingPersons.add(person);
    }

    public void removeMovingPerson(List<PersonTimerInformation> list){
        for(PersonTimerInformation l :list){
            this.movingPersons.remove(l);
        }
    }

    public void addRestingPerson(String person){
        PersonTimerInformation m = new PersonTimerInformation(person, MathOperations.getRandomNumberInRange(10, 1) * 1000L, 0);
        this.restingPersons.add(m);
    }

    public void removeRestingPerson(List<PersonTimerInformation> list){
        Resource personInstance;
        for(PersonTimerInformation l :list){
            this.restingPersons.remove(l);
            personInstance = CareeInfModel.Instance().getResource(l.getPerson());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
        }
    }

    public List<PersonTimerInformation> updatePersonMovement(long deltaTime, List<PersonTimerInformation> list){
        List<PersonTimerInformation> personWhoFinishedMovement = processElapsedTime(deltaTime, list);
        removeMovingPerson(personWhoFinishedMovement);
        return personWhoFinishedMovement;
    }

    public void updatePersonResting(long deltaTime, List<PersonTimerInformation> list){
        List<PersonTimerInformation> personWhoFinishedResting = processElapsedTime(deltaTime, list);
        removeRestingPerson(personWhoFinishedResting);
    }


    private List<PersonTimerInformation> processElapsedTime(long deltaTime, List<PersonTimerInformation> list) {
        List<PersonTimerInformation> personWhoseTimeFinished = new ArrayList<>();
        long getPersonTimeElapsed;
        long getPersonTimeRequired;
        for(PersonTimerInformation pti : list){
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
