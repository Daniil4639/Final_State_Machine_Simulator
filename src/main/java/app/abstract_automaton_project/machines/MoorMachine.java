package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.*;

public class MoorMachine extends Machine {

    private List<String> results;

    public MoorMachine() {
        super();
        this.results = new ArrayList<>();
    }

    public void setParams(List<List<String>> conditionsMatrix,
                          List<String> results,
                          List<String> conditions,
                          List<String> transitions,
                          String startCondition) {

        checkConditionsMatrix(conditionsMatrix, conditions, transitions);
        checkStartCondition(conditions, startCondition);
        checkResults(results, conditions.size());

        setConditionsMatrix(conditionsMatrix);
        this.results = results;
        setConditions(conditions);
        setTransitions(transitions);
        setStartCondition(startCondition);
    }

    public List<String> getResults() {
        return results;
    }

    private void checkResults(List<String> results, int targetSize) {
        if (results.size() != targetSize) {
            throw new WrongMachineParams(String.format(
                    """
                    Несоответствие размеров списка выходных значений и списка состояний!
                    Размер списка состояний: %s
                    Размер списка выходных значений: %s
                    """,
                    targetSize,
                    results.size()
            ));
        }

        for (String result: results) {
            if (!NAME_PATTERN.matcher(result).matches()) {
                throw new WrongMachineParams(String.format(
                        """
                        Выходной сигнал "%s" не соответствует формату ввода.
                        Допустимые символы: a-z A-Z 0-9 _
                        """,
                        result
                ));
            }
        }
    }
}