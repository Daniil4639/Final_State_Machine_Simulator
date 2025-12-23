package app.abstract_automaton_project.processes;

import app.abstract_automaton_project.exceptions.WrongMachineParams;
import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.machines.MoorMachine;

import java.util.ArrayList;
import java.util.List;

public class MoorProcess implements MachineProcessInterface {

    private final MoorMachine moorMachine;

    private final List<String> results;

    private final List<String> conditionsHistory;

    private final List<String> inputsHistory;

    private int currentStep;

    public MoorProcess(MoorMachine moorMachine) {
        this.moorMachine = moorMachine;
        this.results = new ArrayList<>();
        this.conditionsHistory = new ArrayList<>(List.of(moorMachine.getStartCondition()));
        this.inputsHistory = new ArrayList<>();
        this.currentStep = 0;
    }

    @Override
    public void step(String input) {
        int conditionIndex = moorMachine.getConditions().indexOf(getLastConditionFromHistory());
        if (conditionIndex == -1) {
            throw new WrongMachineParams(String.format(
                    """
                    Текущее состояние не найдено:
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

        String nextCondition = getNextCondition(input, transitionIndex, conditionIndex);

        String nestResult = moorMachine.getResults()
                .get(conditionIndex);

        currentStep += 1;
        conditionsHistory.add(nextCondition);
        results.add(nestResult);
        inputsHistory.add(input);
    }

    private String getNextCondition(String input, int transitionIndex, int conditionIndex) {
        String nextCondition = moorMachine.getConditionsMatrix()
                .get(transitionIndex)
                .get(conditionIndex);
        if (nextCondition.equals("-")) {
            throw new WrongMachineParams(String.format(
                    """
                    Заданный переход невозможен. Отсутствует дальнейшее состояние в матрице переходов.
                    "%s" + "%s" -> "%s"
                    """,
                    getCurrentStep(), input, nextCondition
            ));
        }
        return nextCondition;
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
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
        conditionsHistory.add(moorMachine.getStartCondition());
        inputsHistory.clear();
        currentStep = 0;
    }

    @Override
    public String getMachineName() {
        return "Автомат Мура";
    }

    @Override
    public Machine getMachine() {
        return moorMachine;
    }
}