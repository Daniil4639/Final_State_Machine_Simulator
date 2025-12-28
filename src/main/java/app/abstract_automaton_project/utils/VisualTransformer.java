package app.abstract_automaton_project.utils;

import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.processes.MachineProcessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class VisualTransformer {

    public static String getMachineDescription(Machine machine) {
        String printStr = machine.getMachineNamePrint() + System.lineSeparator() +
                "СОСТОЯНИЯ:           " + machine.getConditions() + System.lineSeparator() +
                "ВХОДНОЙ АЛФАВИТ:     " + machine.getTransitions() + System.lineSeparator() +
                "ВЫХОДНОЙ АЛФАВИТ:    " + machine.getResultsList() + System.lineSeparator() +
                "НАЧАЛЬНОЕ СОСТОЯНИЕ: " + machine.getStartCondition() +
                System.lineSeparator() + System.lineSeparator() +
                "ТАБЛИЦА ПЕРЕХОДОВ:" + System.lineSeparator() +
                machine.getConditionsMatrixPrint() +
                System.lineSeparator();

        String results = machine.getResultsPrint();
        if (!results.isEmpty()) {
            return printStr + "ТАБЛИЦА ВЫХОДОВ:" + System.lineSeparator() +
                    machine.getResultsPrint();
        }

        return printStr;
    }

    public static String getResultsDescription(MachineProcessInterface process) {
        return "══════════════════════════════════════════════════════" +
                System.lineSeparator() +
                String.format("       Результаты моделирования (%s)", process.getMachineName()) +
                System.lineSeparator() +
                "══════════════════════════════════════════════════════" +
                System.lineSeparator() + System.lineSeparator() +
                getProcessMatrix(process) + System.lineSeparator() +
                "Входная последовательность: " + process.getInputsHistory() +
                System.lineSeparator() +
                "Последовательность состояний: " + process.getAllConditionsFromHistory() +
                System.lineSeparator() +
                "Выходная последовательность: " + process.getAllResults() +
                System.lineSeparator() +
                "Итоговое состояние: " + process.getLastConditionFromHistory() +
                System.lineSeparator();
    }

    private static String getProcessMatrix(MachineProcessInterface process) {
        StringBuilder builder = new StringBuilder();

        List<String> descriptions = new ArrayList<>(List.of(
                "Вход", "Сост.", "Выход"
        ));
        int descriptionMaxLen = 9;

        List<String> inputs = new ArrayList<>(process.getInputsHistory());
        inputs.add("");
        List<String> conditions = new ArrayList<>(process.getAllConditionsFromHistory());
        List<String> results = new ArrayList<>(process.getAllResults());

        if (process.getMachineName().equals("Автомат Мили")) {
            results.add("");
        } else {
            results.add(0, "");
        }

        List<List<String>> matrix = new ArrayList<>(List.of(inputs, conditions, results));

        int elementsMaxLen = Stream.concat(inputs.stream(),
                        Stream.concat(conditions.stream(), results.stream()))
                .mapToInt(String::length)
                .max()
                .orElse(-4) + 4;

        builder.append("┌").append("─".repeat(descriptionMaxLen));
        for (int i = 0; i < conditions.size(); i++) {
            builder.append("┬").append("─".repeat(elementsMaxLen));
        }
        builder.append("┐").append(System.lineSeparator());

        int mainSymbolPadding = (descriptionMaxLen - 4) / 2;
        builder.append("│").append(" ".repeat(mainSymbolPadding))
                .append("Такт")
                .append(" ".repeat(descriptionMaxLen - 4 - mainSymbolPadding));

        for (int i = 0; i < conditions.size(); i++) {
            int indexPadding = (elementsMaxLen - 1) / 2;
            builder.append("│").append(" ".repeat(indexPadding))
                    .append(i)
                    .append(" ".repeat(elementsMaxLen - 1 - indexPadding));
        }
        builder.append("│").append(System.lineSeparator());

        builder.append("├").append("─".repeat(descriptionMaxLen));
        for (int i = 0; i < conditions.size(); i++) {
            builder.append("┼").append("─".repeat(elementsMaxLen));
        }
        builder.append("┤").append(System.lineSeparator());

        for (int i = 0; i < descriptions.size(); i++) {
            String description = descriptions.get(i);
            List<String> row = matrix.get(i);

            int descriptionPadding = (descriptionMaxLen - description.length()) / 2;
            builder.append("│").append(" ".repeat(descriptionPadding))
                    .append(description)
                    .append(" ".repeat(descriptionMaxLen - description.length() - descriptionPadding));

            for (String element: row) {
                int conditionPadding = (elementsMaxLen - element.length()) / 2;
                builder.append("│").append(" ".repeat(conditionPadding))
                        .append(element)
                        .append(" ".repeat(elementsMaxLen - element.length() - conditionPadding));
            }
            builder.append("│").append(System.lineSeparator());
        }

        builder.append("└").append("─".repeat(descriptionMaxLen));
        for (int i = 0; i < conditions.size(); i++) {
            builder.append("┴").append("─".repeat(elementsMaxLen));
        }
        builder.append("┘").append(System.lineSeparator());

        return builder.toString();
    }
}