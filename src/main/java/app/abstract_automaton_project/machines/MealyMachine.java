package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.*;

public class MealyMachine extends Machine {

    private List<List<String>> resultsMatrix;

    public MealyMachine() {
        super();
        this.resultsMatrix = new ArrayList<>();
    }

    public void setParams(List<List<String>> conditionsMatrix,
                          List<List<String>> resultsMatrix,
                          List<String> conditions,
                          List<String> transitions,
                          String startCondition) {

        checkConditionsList(conditions);
        checkInputsList(transitions);
        checkConditionsMatrix(conditionsMatrix, conditions, transitions);
        checkResultsMatrix(resultsMatrix, conditions, transitions);
        checkStartCondition(conditions, startCondition);

        setConditionsMatrix(conditionsMatrix);
        this.resultsMatrix = resultsMatrix;
        setConditions(conditions);
        setTransitions(transitions);
        setStartCondition(startCondition);
    }

    public List<List<String>> getResultsMatrix() {
        return resultsMatrix;
    }

    private void checkResultsMatrix(List<List<String>> resultsMatrix,
                                    List<String> conditions,
                                    List<String> transitions) {

        if (resultsMatrix.size() != transitions.size()) {
            throw new WrongMachineParams(String.format(
                    """
                    Несоответствие размеров матрицы выходных сигналов и количества известных входных сигналов!
                    Высота матрицы: %s
                    Кол-во входных сигналов: %s
                    """,
                    resultsMatrix.size(),
                    transitions.size()
            ));
        }

        for (List<String> resultsRow: resultsMatrix) {
            if (resultsRow.size() != conditions.size()) {
                throw new WrongMachineParams(String.format(
                        """
                        Несоответствие размеров матрицы выходных сигналов и количества известных состояний!
                        Ширина матрицы: %s
                        Кол-во состояний: %s
                        """,
                        resultsMatrix.size(),
                        transitions.size()
                ));
            }

            for (String result: resultsRow) {
                if (result.isEmpty()) {
                    throw new WrongMachineParams("Выходной сигнал не может быть пустым!");
                }

                if (!result.equals("-") && !NAME_PATTERN.matcher(result).matches()) {
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
}