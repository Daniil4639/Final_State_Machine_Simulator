package app.abstract_automaton_project.machines;

import app.abstract_automaton_project.exceptions.WrongMachineParams;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Machine {

    protected final static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]*");

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

    protected void checkConditionsList(List<String> conditions) {
        if (conditions.isEmpty()) {
            throw new WrongMachineParams("Список состояний не может быть пустым!");
        }

        conditions.forEach(elem -> {
            if (!NAME_PATTERN.matcher(elem).matches()) {
                throw new WrongMachineParams(String.format(
                        """
                        Состояние "%s" не соответствует формату ввода.
                        Допустимые символы: a-z A-Z 0-9 _
                        """,
                        elem
                ));
            }
        });
    }

    protected void checkInputsList(List<String> inputs) {
        if (inputs.isEmpty()) {
            throw new WrongMachineParams("Список входных сигналов не может быть пустым!");
        }

        inputs.forEach(elem -> {
            if (!NAME_PATTERN.matcher(elem).matches()) {
                throw new WrongMachineParams(String.format(
                        """
                        Входной сигнал "%s" не соответствует формату ввода.
                        Допустимые символы: a-z A-Z 0-9 _
                        """,
                        elem
                ));
            }
        });
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
                if (condition.isEmpty()) {
                    throw new WrongMachineParams("Целевое состояние не может быть пустым!");
                }

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

    public abstract String getMachineNamePrint();

    public abstract List<String> getResultsList();

    public abstract String getResultsPrint();

    public abstract String getConditionsMatrixPrint();
}