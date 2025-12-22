package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        }
    }

    public static MealyMachine createMealyMachineConsole() {
        List<String> conditions = createConditionsConsole();
        List<String> transitions = createTransitionsConsole();

        List<List<String>> conditionsMatrix = createConditionsMatrixConsole(conditions, transitions);
        List<List<String>> resultsMatrix = createResultsMatrixConsole(conditions, transitions);

        String startCondition = createStartConditionConsole(conditions);

        MealyMachine mealyMachine = new MealyMachine();
        mealyMachine.setParams(
                conditionsMatrix,
                resultsMatrix,
                conditions,
                transitions,
                startCondition
        );

        return mealyMachine;
    }

    private static List<List<String>> createResultsMatrixConsole(List<String> conditions,
                                                                 List<String> transitions) {

        List<List<String>> resultsMatrix = new ArrayList<>();
        System.out.printf(RESULTS_MATRIX_HINT, conditions.size(), transitions.size());

        for (int ind = 0; ind < transitions.size(); ind++) {
            List<String> resultsRow;

            while (true) {
                resultsRow = Arrays.stream(sc.nextLine()
                                .replace(" ", "")
                                .split(","))
                        .toList();

                boolean ok = true;

                if (resultsRow.size() != conditions.size()) {
                    System.out.printf(
                            """
                            Размер введенной строки: %d. Ожидаемый размер: %d.
                            Введите строку матрицы повторно:
                            """,
                            resultsRow.size(), conditions.size()
                    );
                    continue;
                }

                for (String result: resultsRow) {
                    if (result.equals("-")) {
                        continue;
                    }

                    if (isElementNotMatches(result)) {
                        System.out.println("Введите строку матрицы повторно:");
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    break;
                }
            }

            resultsMatrix.add(resultsRow);
        }

        return resultsMatrix;
    }

    private static final String RESULTS_MATRIX_HINT =
            """
            
            ---------------------------------------------------------------
            
            Заполнение матрицы выходных сигналов!
            Для каждого состояния и входного значения необходимо указать следующее сигналы!
            Использовать '-' в случае отсутствия сигнала!
            
            Пример матрицы выходных сигналов:
            
                       │     q0     │     q1     │     q2     │
              ─────────┼────────────┼────────────┼────────────┼
                  x1   │     y0     │     y1     │     y2     │
                  x2   │     y3     │     y4     │     y5     │
            
            Пример ожидаемого ввода:
            > y0,y1,y2
            > y3,y4,y5
            
            Ожидаемый размер вводимой матрицы на основе введенных ранее состояний и входных сигналов:
            Ширина - %s
            Высота - %s
            
            Ввод матрицы:
            """;
}