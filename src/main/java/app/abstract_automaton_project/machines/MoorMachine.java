package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.*;
import java.util.stream.Stream;

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

    @Override
    public String getMachineNamePrint() {
        return
                """
                ╔════════════════════════════════════════════════════════════════╗
                ║               ТЕКУЩАЯ КОНФИГУРАЦИЯ АВТОМАТА МУРА               ║
                ╚════════════════════════════════════════════════════════════════╝
                """;
    }

    @Override
    public List<String> getResultsList() {
        return results;
    }

    @Override
    public String getResultsPrint() {
        return "";
    }

    @Override
    public String getConditionsMatrixPrint() {
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

        for (String result: getResults()) {
            int resultPadding = (conditionMaxLen - result.length()) / 2;
            builder.append("│").append(" ".repeat(resultPadding))
                    .append(result)
                    .append(" ".repeat(conditionMaxLen - result.length() - resultPadding));
        }
        builder.append("│").append(System.lineSeparator());

        builder.append("├").append("─".repeat(transitionMaxLen));
        for (int i = 0; i < getConditions().size(); i++) {
            builder.append("┼").append("─".repeat(conditionMaxLen));
        }
        builder.append("┤").append(System.lineSeparator());

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