package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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