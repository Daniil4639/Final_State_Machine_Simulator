package app.abstract_automaton_project.processes;

import app.abstract_automaton_project.exceptions.WrongMachineParams;
import app.abstract_automaton_project.machines.MealyMachine;

import java.util.ArrayList;
import java.util.List;

public class MealyProcess implements MachineProcessInterface {

    private final MealyMachine mealyMachine;

    private final List<String> results;

    private final List<String> conditionsHistory;

    private int currentStep;

    public MealyProcess(MealyMachine mealyMachine) {
        this.mealyMachine = mealyMachine;
        this.results = new ArrayList<>();
        this.conditionsHistory = new ArrayList<>(List.of(this.mealyMachine.getStartCondition()));
        this.currentStep = 0;
    }

    @Override
    public void step(String input) {
        int conditionIndex = mealyMachine.getConditions().indexOf(getLastConditionFromHistory());
        if (conditionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                            Начальное состояние не найдено:
                            %s
                            Известные состояния:
                            %s
                    """,
                    getLastConditionFromHistory(),
                    mealyMachine.getConditions()
            ));
        }

        int transitionIndex = mealyMachine.getTransitions().indexOf(input);
        if (transitionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                            Заданное входное значение отсутствуют в списке входных сигналов:
                            %s
                            Известные входные сигналы:
                            %s
                    """,
                    input,
                    mealyMachine.getTransitions()
            ));
        }

        String nextCondition = mealyMachine.getConditionsMatrix()
                .get(transitionIndex)
                .get(conditionIndex);
        String nestResult = mealyMachine.getResultsMatrix()
                .get(transitionIndex)
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
        conditionsHistory.add(mealyMachine.getStartCondition());
        currentStep = 0;
    }
}