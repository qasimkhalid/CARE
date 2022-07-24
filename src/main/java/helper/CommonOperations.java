package helper;

import model.PersonMovementInformation;

import java.util.ArrayList;
import java.util.List;

public class CommonOperations {

    public static List<PersonMovementInformation> processStepBasedElapsedTime(long deltaTime, List<PersonMovementInformation> list) {
        List<PersonMovementInformation> personWhoseTimeFinished = new ArrayList<>();
        long getPersonTimeElapsed;
        long getPersonTimeRequired;
        for(PersonMovementInformation pti : list){
            getPersonTimeElapsed = pti.getStepBasedTimeElapsed();
            getPersonTimeRequired = pti.getStepBasedTimeRequired();
            if (getPersonTimeRequired > getPersonTimeElapsed){
                pti.setStepBasedTimeElapsed(getPersonTimeElapsed + deltaTime);
            } else {
                personWhoseTimeFinished.add(pti);
            }
        }
        return personWhoseTimeFinished;
    }
}
