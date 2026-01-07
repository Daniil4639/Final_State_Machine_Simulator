package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.*;
import java.util.stream.Stream;

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

    @Override
    public String getMachineNamePrint() {
        return
                """
                ╔════════════════════════════════════════════════════════════════╗
                ║               ТЕКУЩАЯ КОНФИГУРАЦИЯ АВТОМАТА МИЛИ               ║
                ╚════════════════════════════════════════════════════════════════╝
                """;
    }

    @Override
    public List<String> getResultsList() {
        Set<String> results = new HashSet<>();
        for (List<String> resultsRow: resultsMatrix) {
            results.addAll(resultsRow);
        }

        return results.stream()
                .sorted()
                .toList();
    }

    @Override
    public String getResultsPrint() {
        int conditionMaxLen = Stream.concat(getConditions().stream(),
                        getResultsList().stream())
                .mapToInt(String::length)
                .max()
                .orElse(-4) + 4;

        int transitionMaxLen = getTransitions().stream()
                .mapToInt(String::length)
                .max()
                .orElse(-4) + 4;

        StringBuilder builder = new StringBuilder();
        builder.append("┌").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┬").append("─".repeat(conditionMaxLen));
        }
        builder.append("┐").append(System.lineSeparator());

        int mainSymbolPadding = (transitionMaxLen - 1) / 2;
        builder.append("│").append(" ".repeat(mainSymbolPadding))
                .append("λ")
                .append(" ".repeat(transitionMaxLen - 1 - mainSymbolPadding));

        for (String condition: getConditions()) {
            int conditionPadding = (conditionMaxLen - condition.length()) / 2;
            builder.append("│").append(" ".repeat(conditionPadding))
                    .append(condition)
                    .append(" ".repeat(conditionMaxLen - condition.length() - conditionPadding));
        }
        builder.append("│").append(System.lineSeparator());

        builder.append("├").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┼").append("─".repeat(conditionMaxLen));
        }
        builder.append("┤").append(System.lineSeparator());

        for (int i = 0; i < getTransitions().size(); i++) {
            String transition = getTransitions().get(i);
            List<String> resultsRow = getResultsMatrix().get(i);

            int transitionPadding = (transitionMaxLen - transition.length()) / 2;
            builder.append("│").append(" ".repeat(transitionPadding))
                    .append(transition)
                    .append(" ".repeat(transitionMaxLen - transition.length() - transitionPadding));

            for (String result: resultsRow) {
                int conditionPadding = (conditionMaxLen - result.length()) / 2;
                builder.append("│").append(" ".repeat(conditionPadding))
                        .append(result)
                        .append(" ".repeat(conditionMaxLen - result.length() - conditionPadding));
            }
            builder.append("│").append(System.lineSeparator());
        }

        builder.append("└").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┴").append("─".repeat(conditionMaxLen));
        }
        builder.append("┘").append(System.lineSeparator());

        return builder.toString();
    }

    @Override
    public String getConditionsMatrixPrint() {
        int conditionMaxLen = getConditions().stream()
                .mapToInt(String::length)
                .max()
                .orElse(-4) + 4;

        int transitionMaxLen = getTransitions().stream()
                .mapToInt(String::length)
                .max()
                .orElse(-4) + 4;

        StringBuilder builder = new StringBuilder();
        builder.append("┌").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┬").append("─".repeat(conditionMaxLen));
        }
        builder.append("┐").append(System.lineSeparator());

        int mainSymbolPadding = (transitionMaxLen - 1) / 2;
        builder.append("│").append(" ".repeat(mainSymbolPadding))
                .append("δ")
                .append(" ".repeat(transitionMaxLen - 1 - mainSymbolPadding));

        for (String condition: getConditions()) {
            int conditionPadding = (conditionMaxLen - condition.length()) / 2;
            builder.append("│").append(" ".repeat(conditionPadding))
                    .append(condition)
                    .append(" ".repeat(conditionMaxLen - condition.length() - conditionPadding));
        }
        builder.append("│").append(System.lineSeparator());

        builder.append("├").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┼").append("─".repeat(conditionMaxLen));
        }
        builder.append("┤").append(System.lineSeparator());

        for (int i = 0; i < getTransitions().size(); i++) {
            String transition = getTransitions().get(i);
            List<String> conditionsRow = getConditionsMatrix().get(i);

            int transitionPadding = (transitionMaxLen - transition.length()) / 2;
            builder.append("│").append(" ".repeat(transitionPadding))
                    .append(transition)
                    .append(" ".repeat(transitionMaxLen - transition.length() - transitionPadding));

            for (String condition: conditionsRow) {
                int conditionPadding = (conditionMaxLen - condition.length()) / 2;
                builder.append("│").append(" ".repeat(conditionPadding))
                        .append(condition)
                        .append(" ".repeat(conditionMaxLen - condition.length() - conditionPadding));
            }
            builder.append("│").append(System.lineSeparator());
        }

        builder.append("└").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┴").append("─".repeat(conditionMaxLen));
        }
        builder.append("┘").append(System.lineSeparator());

        return builder.toString();
    }
}