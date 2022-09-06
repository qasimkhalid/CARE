package helper;

import model.CareeInfModel;

import java.util.List;

public abstract class IEvacuationPlan {

    // Calls only once
    public abstract void Execute();

    // Calls every time step
    public void Update(long timeStep) {

    }

    // returns true if this plan is still in progress
    public boolean inProgress(int totalPersons) {
        // a query that returns all the persons located in the building other than the
        // exits
        /* e.g. return BuildingUtils.hasAnyPerson(); */
        List<String> personsWhoHaveNotEvacuated = CareeInfModel.Instance()
                .getQueryResult("data/Queries/sparql/PersonsWhoHaveEvacuated.txt");
        return personsWhoHaveNotEvacuated.size() != totalPersons;
    }

    // it should be abstract
    public void Interrupt(/* List<Space> spacesWithInterrupts */) {

        //

        // may be it has some parameters or information from your ontology that has some
        // resonable data
        // based on that data you can regenerate the paths for evacuating persons
    }
}