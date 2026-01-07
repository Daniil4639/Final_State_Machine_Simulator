package app.abstract_automaton_project.utils;

import app.abstract_automaton_project.exceptions.ReadMachineException;
import app.abstract_automaton_project.exceptions.SaveMachineException;
import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.machines.MealyMachine;
import app.abstract_automaton_project.machines.MoorMachine;
import app.abstract_automaton_project.processes.MachineProcessInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MachineFileTransformer {

    private final static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]*");

    public static void saveProcessResultsToFile(MachineProcessInterface process, Path filepath) {
        save(filepath, VisualTransformer.getResultsDescription(process));
    }

    public static Machine getMachineFromFile(Path filePath) {
        String data = readData(filePath);

        Map<String, String> params = Arrays.stream(data.split(";"))
                .map(String::trim)
                .filter(entry -> entry.contains(":"))
                .collect(Collectors.toMap(
                      entry -> entry.substring(0, entry.indexOf(':')),
                      entry -> entry.substring(entry.indexOf(':') + 1),
                        (_, newValue) -> newValue,
                        LinkedHashMap::new
                ));

        String machineName = extractMachineName(params);
        List<String> conditions = extractConditions(params);
        List<String> inputs = extractInputs(params);
        List<List<String>> conditionsMatrix = extractConditionsMatrix(params,
                conditions.size(), inputs.size());

        for (List<String> conditionsRow: conditionsMatrix) {
            for (String condition: conditionsRow) {
                if (!conditionsRow.contains(condition)) {
                    throw new ReadMachineException(String.format(
                            "Состояние \"%s\" не присутствует в списке доступных состояний",
                            condition
                    ));
                }
            }
        }
        String startCondition = extractStartCondition(params);

        if (machineName.equals("1")) {
            List<List<String>> results = extractResultsMatrix(params, conditions.size(), inputs.size());

            MealyMachine machine = new MealyMachine();
            machine.setParams(
                    conditionsMatrix, results,
                    conditions, inputs,
                    startCondition
            );

            return machine;
        } else {
            List<String> results = extractResultsList(params, conditions.size());

            MoorMachine machine = new MoorMachine();
            machine.setParams(
                    conditionsMatrix, results,
                    conditions, inputs,
                    startCondition
            );

            return machine;
        }
    }

    public static void saveMachineToFIle(Machine machine, Path filePath) {
        StringBuilder builder = new StringBuilder();

        if (machine instanceof MealyMachine mealyMachine) {
            saveMealy(mealyMachine, builder);
        } else {
            saveMoor((MoorMachine) machine, builder);
        }

        String result = builder.toString()
                .replaceAll(" ", "");

        save(filePath, result);
    }

    private static List<List<String>> extractResultsMatrix(Map<String, String> params, int width, int height) {
        String strResultsMatrix = params.get("results");

        if (strResultsMatrix == null) {
            errorMissingValue("results");
        }
        List<String> matrixFlatten = Arrays.stream(strResultsMatrix.split(","))
                .map(String::trim)
                .toList();

        for (String result: matrixFlatten) {
            if (!result.equals("-") && !NAME_PATTERN.matcher(result).matches()) {
                throw new ReadMachineException(String.format(
                        """
                        Выходной сигнал "%s" не подходит по формату.
                        Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                        """,
                        result
                ));
            }
        }

        if (matrixFlatten.size() != (width * height)) {
            throw new ReadMachineException(String.format(
                    """
                    Ожидается размер матрицы выходных значений: %d * %d = %d
                    Получен размер: %d
                    """,
                    width, height, width * height, matrixFlatten.size()
            ));
        }

        List<List<String>> resultsMatrix = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            resultsMatrix.add(matrixFlatten.subList(i * width, (i + 1) * width));
        }

        return resultsMatrix;
    }

    private static List<String> extractResultsList(Map<String, String> params, int width) {
        String strResults = params.get("results");
        if (strResults == null) {
            errorMissingValue("results");
        }

        List<String> results = Arrays.stream(strResults.split(","))
                .map(String::trim)
                .toList();

        for (String result: results) {
            if (!result.equals("-") && !NAME_PATTERN.matcher(result).matches()) {
                throw new ReadMachineException(String.format(
                        """
                        Выходной сигнал "%s" не подходит по формату.
                        Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                        """,
                        result
                ));
            }
        }

        if (results.size() != width) {
            throw new ReadMachineException(String.format(
                    """
                    Ожидается размер списка выходных значений: %d
                    Получен размер: %d
                    """,
                    width, results.size()
            ));
        }

        return results;
    }

    private static List<List<String>> extractConditionsMatrix(Map<String, String> params, int width, int height) {
        String strConditionsMatrix = params.get("conditionsMatrix");

        if (strConditionsMatrix == null) {
            errorMissingValue("conditionsMatrix");
        }
        List<String> matrixFlatten = Arrays.stream(strConditionsMatrix.split(","))
                .map(String::trim)
                .toList();

        for (String condition: matrixFlatten) {
            if (!condition.equals("-") && !NAME_PATTERN.matcher(condition).matches()) {
                throw new ReadMachineException(String.format(
                        """
                        Состояние "%s" не подходит по формату.
                        Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                        """,
                        condition
                ));
            }
        }

        if (matrixFlatten.size() != (width * height)) {
            throw new ReadMachineException(String.format(
                    """
                    Ожидается размер матрицы состояний: %d * %d = %d
                    Получен размер: %d
                    """,
                    width, height, width * height, matrixFlatten.size()
            ));
        }

        List<List<String>> conditionMatrix = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            conditionMatrix.add(matrixFlatten.subList(i * width, (i + 1) * width));
        }

        return conditionMatrix;
    }

    private static String extractStartCondition(Map<String, String> params) {
        String startCondition = params.get("startCondition");

        if (startCondition == null) {
            errorMissingValue("startCondition");
        }
        if (!NAME_PATTERN.matcher(startCondition).matches()) {
            throw new ReadMachineException(String.format(
                    """
                    Начальное состояние "%s" не подходит по формату.
                    Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                    """,
                    startCondition
            ));
        }

        return startCondition;
    }

    private static List<String> extractInputs(Map<String, String> params) {
        String strInputs = params.get("inputs");

        if (strInputs == null) {
            errorMissingValue("inputs");
        }
        List<String> inputs = Arrays.stream(strInputs.split(","))
                .map(String::trim)
                .toList();

        for (String input: inputs) {
            if (!NAME_PATTERN.matcher(input).matches()) {
                throw new ReadMachineException(String.format(
                        """
                        Входное значение "%s" не подходит по формату.
                        Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                        """,
                        input
                ));
            }
        }

        return inputs;
    }

    private static List<String> extractConditions(Map<String, String> params) {
        String strConditions = params.get("conditions");

        if (strConditions == null) {
            errorMissingValue("conditions");
        }
        List<String> conditions = Arrays.stream(strConditions.split(","))
                .map(String::trim)
                .toList();

        for (String condition: conditions) {
            if (!NAME_PATTERN.matcher(condition).matches()) {
                throw new ReadMachineException(String.format(
                        """
                        Состояние "%s" не подходит по формату.
                        Допустимые символы: ['a' - 'z'], ['A' - 'z'], ['0' - '9'], '_'
                        """,
                        condition
                ));
            }
        }

        return conditions;
    }

    private static String extractMachineName(Map<String, String> params) {
        String machineName = params.get("machineType");

        if (machineName == null) {
            errorMissingValue("machineType");
        }
        if (!machineName.equals("1") && !machineName.equals("2")) {
            throw new ReadMachineException("Параметр \"machineType\" должен иметь значения [1 / 2]");
        }

        return machineName;
    }

    private static void errorMissingValue(String valueName) {
        throw new ReadMachineException(String.format(
                "Ошибка чтения файла: отсутствует параметр автомата \"%s\".", valueName
        ));
    }

    private static String readData(Path filePath) {
        List<String> data;

        try {
            data = Files.readAllLines(filePath);
        } catch (IOException ex) {
            throw new ReadMachineException(ex.getMessage());
        }

        return data.stream()
                .reduce(String::concat)
                .orElse("")
                .replaceAll("\\s+", "")
                .replaceAll("\\n", "")
                .replaceAll("\\r", "")
                .replaceAll("\\t", "");
    }

    private static void save(Path filePath, String data) {
        try {
            Files.writeString(filePath, data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (Exception ex) {
            throw new SaveMachineException(ex.getMessage());
        }
    }

    private static void saveMealy(MealyMachine machine, StringBuilder builder) {
        builder.append("machineType:").append(1)
                .append(";")
                .append(System.lineSeparator());
        builder.append("conditions:")
                .append(machine.getConditions()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("inputs:")
                .append(machine.getTransitions()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("conditionsMatrix:")
                .append(machine.getConditionsMatrix().stream()
                        .flatMap(List::stream)
                        .toList()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("results:")
                .append(machine.getResultsMatrix().stream()
                        .flatMap(List::stream)
                        .toList()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("startCondition:")
                .append(machine.getStartCondition())
                .append(";");
    }

    private static void saveMoor(MoorMachine machine, StringBuilder builder) {
        builder.append("machineType:").append(2)
                .append(";")
                .append(System.lineSeparator());
        builder.append("conditions:")
                .append(machine.getConditions()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("inputs:")
                .append(machine.getTransitions()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("conditionsMatrix:")
                .append(machine.getConditionsMatrix().stream()
                        .flatMap(List::stream)
                        .toList()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("results:")
                .append(machine.getResults()
                        .toString()
                        .replaceAll("[\\[\\]]", ""))
                .append(";")
                .append(System.lineSeparator());
        builder.append("startCondition:")
                .append(machine.getStartCondition())
                .append(";");
    }
}