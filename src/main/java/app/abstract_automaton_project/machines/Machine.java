package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Machine {

    protected final static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]*");

    protected final static Scanner sc = new Scanner(System.in);

    protected List<List<String>> conditionsMatrix;

    protected List<String> conditions;

    protected List<String> transitions;

    protected String startCondition;

    protected Machine() {
        this.conditionsMatrix = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.transitions = new ArrayList<>();
        startCondition = null;
    }

    protected void setConditionsMatrix(List<List<String>> conditionsMatrix) {
        this.conditionsMatrix = conditionsMatrix;
    }

    protected void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    protected void setTransitions(List<String> transitions) {
        this.transitions = transitions;
    }

    protected void setStartCondition(String startCondition) {
        this.startCondition = startCondition;
    }

    public List<List<String>> getConditionsMatrix() {
        return conditionsMatrix;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public List<String> getTransitions() {
        return transitions;
    }

    public String getStartCondition() {
        return startCondition;
    }

    protected void checkConditionsMatrix(List<List<String>> conditionsMatrix,
                                       List<String> conditions,
                                       List<String> transitions) {

        List<String> wrongConditions = new ArrayList<>();

        if (conditionsMatrix.size() != transitions.size()) {
            throw new WrongMachineParams(String.format(
                    """
                    Несоответствие размеров матрицы переходов и количества известных входных сигналов!
                    Высота матрицы: %s
                    Кол-во входных сигналов: %s
                    """,
                    conditionsMatrix.size(),
                    transitions.size()
            ));
        }

        for (List<String> conditionsRow: conditionsMatrix) {
            if (conditionsRow.size() != conditions.size()) {
                throw new WrongMachineParams(String.format(
                        """
                        Несоответствие размеров матрицы переходов и количества известных состояний!
                        Ширина матрицы: %s
                        Кол-во состояний: %s
                        """,
                        conditionsRow.size(),
                        conditions.size()
                ));
            }

            for (String condition: conditionsRow) {
                if (!condition.equals("-") && !conditions.contains(condition)) {
                    wrongConditions.add(condition);
                }
            }
        }

        if (!wrongConditions.isEmpty()) {
            throw new WrongMachineParams(String.format(
                    """
                    Заданные элементы отсутствуют в списке возможных состояний:
                    %s
                    Известные состояния:
                    %s
                    """,
                    wrongConditions,
                    conditions
            ));
        }
    }

    protected static boolean isElementNotMatches(String element) {
        Matcher matcher = NAME_PATTERN.matcher(element);

        if (!matcher.matches()) {
            System.out.printf(
                    """
                    Элемент "%s" не подходит по формату.
                    Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                    """,
                    element
            );

            return true;
        }

        return false;
    }

    protected static boolean isElementNotOnConditionsList(String element, List<String> conditions) {
        if (!conditions.contains(element)) {
            System.out.printf(
                    """
                    Элемент "%s" отсутствует в списке возможных состояний.
                    """,
                    element
            );

            return true;
        }

        return false;
    }

    protected void checkStartCondition(List<String> conditions, String condition) {
        if (!conditions.contains(condition)) {
            throw new WrongMachineParams(String.format(
                    """
                    Стартовое состояние "%s" отсутствует в списке возможных!
                    Известные состояния:
                    %s
                    """,
                    condition,
                    conditions
            ));
        }
    }

    protected static List<String> createConditionsConsole() {
        List<String> conditions;
        System.out.println("\n---------------------------------------------------------------\n");

        while (true) {
            System.out.println("Введите через запятую список состояний (например: q0,q1,q2,q3):");
            conditions = Arrays.stream(sc.nextLine()
                            .replace(" ", "")
                            .split(","))
                    .toList();

            boolean ok = true;
            for (String condition: conditions) {
                if (isElementNotMatches(condition)) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                break;
            }
        }

        return conditions;
    }

    protected static List<String> createTransitionsConsole() {
        List<String> transitions;
        System.out.println("\n---------------------------------------------------------------\n");

        while (true) {
            System.out.println("Введите через запятую список входных сигналов (например: x0,x1,x2,x3):");
            transitions = Arrays.stream(sc.nextLine()
                            .replace(" ", "")
                            .split(","))
                    .toList();

            boolean ok = true;
            for (String transition: transitions) {
                if (isElementNotMatches(transition)) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                break;
            }
        }

        return transitions;
    }
    
    protected static String createStartConditionConsole(List<String> conditions) {
        String condition;
        System.out.println("\n---------------------------------------------------------------\n");

        while (true) {
            System.out.println("Введите начальное состояние автомата (например: q0):");
            condition = sc.nextLine();
            
            if (!conditions.contains(condition)) {
                System.out.println("Введенное состояние отсутствует в списке возможных состояний!");
            } else {
                break;
            }
        }

        return condition;
    }

    protected static List<List<String>> createConditionsMatrixConsole(List<String> conditions,
                                                                      List<String> transitions) {

        List<List<String>> conditionsMatrix = new ArrayList<>();
        System.out.printf(CONDITIONS_MATRIX_HINT, conditions.size(), transitions.size());

        for (int ind = 0; ind < transitions.size(); ind++) {
            List<String> conditionsRow;

            while (true) {
                conditionsRow = Arrays.stream(sc.nextLine()
                                .replace(" ", "")
                                .split(","))
                        .toList();

                boolean ok = true;

                if (conditionsRow.size() != conditions.size()) {
                    System.out.printf(
                            """
                            Размер введенной строки: %d. Ожидаемый размер: %d.
                            Введите строку матрицы повторно:
                            """,
                            conditionsRow.size(), conditions.size()
                    );
                    continue;
                }

                for (String condition: conditionsRow) {
                    if (condition.equals("-")) {
                        continue;
                    }

                    if (isElementNotMatches(condition) || isElementNotOnConditionsList(condition, conditions)) {
                        System.out.println("Введите строку матрицы повторно:");
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    break;
                }
            }

            conditionsMatrix.add(conditionsRow);
        }

        return conditionsMatrix;
    }

    private static final String CONDITIONS_MATRIX_HINT =
            """
            
            ---------------------------------------------------------------
            
            Заполнение матрицы переходов!
            Для каждого состояния и входного значения необходимо указать следующее состояние!
            Использовать '-' в случае отсутствия перехода!
            
            Пример матрицы переходов:
            
                       │     q0     │     q1     │     q2     │
              ─────────┼────────────┼────────────┼────────────┼
                  x1   │     q0     │     q2     │     q1     │
                  x2   │     q1     │      -     │     q0     │
            
            Пример ожидаемого ввода:
            > q0,q2,q1
            > q1,-,q0
            
            Ожидаемый размер вводимой матрицы на основе введенных ранее состояний и входных сигналов:
            Ширина - %s
            Высота - %s
            
            Ввод матрицы:
            """;
}