package app.abstract_automaton_project.processes;

import app.abstract_automaton_project.exceptions.WrongMachineParams;
import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.machines.MealyMachine;

import java.util.ArrayList;
import java.util.List;

public class MealyProcess implements MachineProcessInterface {

    private final MealyMachine mealyMachine;

    private final List<String> results;

    private final List<String> conditionsHistory;

    private final List<String> inputsHistory;

    public MealyProcess(MealyMachine mealyMachine) {
        this.mealyMachine = mealyMachine;
        this.results = new ArrayList<>();
        this.conditionsHistory = new ArrayList<>(List.of(this.mealyMachine.getStartCondition()));
        this.inputsHistory = new ArrayList<>();
    }

    @Override
    public void step(String input) {
        int conditionIndex = mealyMachine.getConditions().indexOf(getLastConditionFromHistory());
        if (conditionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                    Текущее состояние не найдено:
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

        String nextCondition = getNextCondition(input, transitionIndex, conditionIndex);

        String nestResult = mealyMachine.getResultsMatrix()
                .get(transitionIndex)
                .get(conditionIndex);

        conditionsHistory.add(nextCondition);
        results.add(nestResult);
        inputsHistory.add(input);
    }

    private String getNextCondition(String input, int transitionIndex, int conditionIndex) {
        String nextCondition = mealyMachine.getConditionsMatrix()
                .get(transitionIndex)
                .get(conditionIndex);
        if (nextCondition.equals("-")) {
            throw new WrongMachineParams(String.format(
                    """
                    Заданный переход невозможен. Отсутствует дальнейшее состояние в матрице переходов.
                    "%s" + "%s" -> "%s"
                    """,
                    mealyMachine.getConditions().get(conditionIndex), input, nextCondition
            ));
        }
        return nextCondition;
    }

    @Override
    public String getLastResult() {
        if (results.isEmpty()) {
            return "-";
        }

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
    public List<String> getInputsHistory() {
        return inputsHistory;
    }

    @Override
    public String getLastInput() {
        if (inputsHistory.isEmpty()) {
            return "-";
        }

        return inputsHistory.get(inputsHistory.size() - 1);
    }

    @Override
    public void clearProcess() {
        results.clear();
        conditionsHistory.clear();
        conditionsHistory.add(mealyMachine.getStartCondition());
        inputsHistory.clear();
    }

    @Override
    public String getMachineName() {
        return "Автомат Мили";
    }

    @Override
    public Machine getMachine() {
        return mealyMachine;
    }
}