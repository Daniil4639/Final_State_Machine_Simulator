package app.abstract_automaton_project.processes;

import app.abstract_automaton_project.exceptions.WrongMachineParams;
import app.abstract_automaton_project.machines.MoorMachine;

import java.util.ArrayList;
import java.util.List;

public class MoorProcess implements MachineProcessInterface {

    private final MoorMachine moorMachine;

    private final List<String> results;

    private final List<String> conditionsHistory;

    private int currentStep;

    public MoorProcess(MoorMachine moorMachine) {
        this.moorMachine = moorMachine;
        this.results = new ArrayList<>();
        this.conditionsHistory = new ArrayList<>(List.of(moorMachine.getStartCondition()));
        this.currentStep = 0;
    }

    @Override
    public void step(String input) {
        int conditionIndex = moorMachine.getConditions().indexOf(getLastConditionFromHistory());
        if (conditionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                            Начальное состояние не найдено:
                            %s
                            Известные состояния:
                            %s
                    """,
                    getLastConditionFromHistory(),
                    moorMachine.getConditions()
            ));
        }

        int transitionIndex = moorMachine.getTransitions().indexOf(input);
        if (transitionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                            Заданное входное значение отсутствуют в списке входных сигналов:
                            %s
                            Известные входные сигналы:
                            %s
                    """,
                    input,
                    moorMachine.getTransitions()
            ));
        }

        String nextCondition = moorMachine.getConditionsMatrix()
                .get(transitionIndex)
                .get(conditionIndex);
        String nestResult = moorMachine.getResults()
                .get(conditionIndex);

        currentStep += 1;
        conditionsHistory.add(nextCondition);
        results.add(nestResult);
    }

    @Override
    public void run(List<String> inputs) {
        for (String input: inputs) {
            step(input);
        }
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    public String getLastResult() {
        return results.get(results.size() - 1);
    }

    @Override
    public List<String> getAllResults() {
        return results;
    }

    @Override
    public String getLastConditionFromHistory() {
        return conditionsHistory.get(conditionsHistory.size() - 1);
    }

    @Override
    public List<String> getAllConditionsFromHistory() {
        return conditionsHistory;
    }

    @Override
    public void clearProcess() {
        results.clear();
        conditionsHistory.clear();
        conditionsHistory.add(moorMachine.getStartCondition());
        currentStep = 0;
    }
}