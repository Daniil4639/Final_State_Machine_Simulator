package app.abstract_automaton_project.processes;

import app.abstract_automaton_project.machines.Machine;

import java.util.List;

public interface MachineProcessInterface {

    void step(String input);

    String getLastResult();

    List<String> getAllResults();

    String getLastConditionFromHistory();

    List<String> getInputsHistory();

    String getLastInput();

    List<String> getAllConditionsFromHistory();

    void clearProcess();

    String getMachineName();

    Machine getMachine();

    default int getTactNumber() {
        return getAllConditionsFromHistory().size();
    }
}