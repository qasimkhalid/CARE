package model.scheduler;

import com.hp.hpl.jena.rdf.model.Resource;
import helper.CommonOperations;
import helper.HelpingVariables;
import helper.MathOperations;
import model.CareeInfModel;
import model.PersonMovementInformation;

import java.util.ArrayList;
import java.util.List;

public class RestingScheduler {

    private List<PersonMovementInformation> restingPersons = new ArrayList<>();

    public RestingScheduler() {
    }

    public List<PersonMovementInformation> getRestingPersons() {
        return restingPersons;
    }

    public void setRestingPersons( List<PersonMovementInformation> restingPersons ) {
        this.restingPersons = restingPersons;
    }

//    public void addRestingPerson(String person){
//        PersonMovementInformation m = new PersonMovementInformation(person, MathOperations.getRandomNumberInRange(10, 1) * 1000L, 0);
//        this.restingPersons.add(m);
//    }

    public void removeRestingPerson(List<PersonMovementInformation> list){
        Resource personInstance;
        for(PersonMovementInformation l :list){
            this.restingPersons.remove(l);
            personInstance = CareeInfModel.Instance().getResource(l.getPerson());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
        }
    }

    public void updatePersonResting(long deltaTime, List<PersonMovementInformation> list){
        List<PersonMovementInformation> personWhoFinishedResting = CommonOperations.processStepBasedElapsedTime(deltaTime, list);
        removeRestingPerson(personWhoFinishedResting);
    }
}

