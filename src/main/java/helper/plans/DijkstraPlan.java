package helper.plans;

import helper.IEvacuationPlan;

public class DijkstraPlan extends IEvacuationPlan {

    @Override
    public void Execute() {
        // TODO Auto-generated method stub
        // get all exits
        // find persons's locations using BFS from exits
        // it will return us paths from person to exit
        // start moving the person
    }

    @Override
    public void Update(long timeStep) {
        // TODO Auto-generated method stub
        // here check in update if a person has moved to another room, update its
        // location to new room
    }

    @Override
    public void Interrupt() {
        // if interrupt happens find people who are using that space and do the
        // following again
        // find affected persons' locations using BFS from exits
        // it will return us paths from person to exit
        // start moving the person
    }
}
