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
        checkResults(conditions, results);

        setConditionsMatrix(conditionsMatrix);
        this.results = results;
        setConditions(conditions);
        setTransitions(transitions);
        setStartCondition(startCondition);
    }

    public List<String> getResults() {
        return results;
    }

    private void checkResults(List<String> conditions, List<String> results) {
        if (conditions.size() != results.size()) {
            throw new WrongMachineParams(String.format(
                    """
                    Несоответствие размеров списка выходных значений и списка состояний!
                    Размер списка состояний: %s
                    Размер списка выходных значений: %s
                    """,
                    conditions.size(),
                    results.size()
            ));
        }
    }

    public static MoorMachine createMoorMachineConsole() {
        List<String> conditions = createConditionsConsole();
        List<String> transitions = createTransitionsConsole();

        List<List<String>> conditionsMatrix = createConditionsMatrixConsole(conditions, transitions);
        List<String> results = createResultsConsole(conditions);

        String startCondition = createStartConditionConsole(conditions);

        MoorMachine moorMachine = new MoorMachine();
        moorMachine.setParams(
                conditionsMatrix,
                results,
                conditions,
                transitions,
                startCondition
        );

        return moorMachine;
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

    private static List<String> createResultsConsole(List<String> conditions) {
        List<String> results;
        System.out.println("\n---------------------------------------------------------------\n");

        while (true) {
            System.out.printf(
                    """
                    Введите через запятую список выходных сигналов (например: y0,y1,y2,y3):
                    (ожидаемый размер списка: %d)
                    """,
                    conditions.size()
            );

            results = Arrays.stream(sc.nextLine()
                            .replace(" ", "")
                            .split(","))
                    .toList();

            if (results.size() != conditions.size()) {
                System.out.printf(
                        """
                        Размер введенной строки: %d. Ожидаемый размер: %d.
                        """,
                        results.size(), conditions.size()
                );
                continue;
            }

            boolean ok = true;
            for (String result: results) {
                if (isElementNotMatches(result)) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                break;
            }
        }

        return results;
    }
}