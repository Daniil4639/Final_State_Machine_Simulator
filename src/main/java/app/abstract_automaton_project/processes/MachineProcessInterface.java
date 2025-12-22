package app.abstract_automaton_project.processes;

import java.util.List;

public interface MachineProcessInterface {

    void step(String input);

    void run(List<String> inputs);

    int getCurrentStep();

    String getLastResult();

    List<String> getAllResults();

    String getLastConditionFromHistory();

    List<String> getAllConditionsFromHistory();

    void clearProcess();
}