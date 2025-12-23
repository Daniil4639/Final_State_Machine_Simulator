package app.abstract_automaton_project.console;

import app.abstract_automaton_project.exceptions.WrongMachineParams;
import app.abstract_automaton_project.processes.MachineProcessInterface;
import app.abstract_automaton_project.utils.VisualTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleCommands {

    private final static Scanner sc = new Scanner(System.in);

    public static void show(MachineProcessInterface machineProcess) {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Что необходимо вывести:
                    [1] - Конфигурация автомата
                    [2] - Текущие результаты моделирования
                """
        );

        int printNumber;

        while (true) {
            String symbol = sc.nextLine();
            try {
                printNumber = Integer.parseInt(symbol);
            } catch (NumberFormatException ex) {
                System.out.println("Не удалось распознать номер. Повторите [1/2]:");
                continue;
            }

            if (printNumber != 1 && printNumber != 2) {
                System.out.println("Введен некорректный номер. Повторите [1/2]:");
            } else {
                break;
            }
        }

        if (printNumber == 1) {
            System.out.println(VisualTransformer.getMachineDescription(machineProcess.getMachine()));
        } else {
            System.out.println(VisualTransformer.getResultsDescription(machineProcess));
        }
    }

    public static void run(MachineProcessInterface machineProcess) {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Введите последовательность входных сигналов (например: x1, x2, x3, x4):
                """
        );

        List<String> inputs = Arrays.stream(sc.nextLine()
                        .replace(" ", "")
                        .split(","))
                .toList();
        System.out.println();

        for (int ind = 0; ind < inputs.size(); ind++) {
            String input = inputs.get(ind);
            String currentCondition = machineProcess.getLastConditionFromHistory();
            System.out.printf("───── Такт %d ─────\n", ind);

            try {
                machineProcess.step(input);

                System.out.printf("Вход: \"%s\". Переход: \"%s\" -> \"%s\". Выход: \"%s\".\n",
                        input, currentCondition,
                        machineProcess.getLastConditionFromHistory(),
                        machineProcess.getLastResult());
            } catch (WrongMachineParams ex) {
                System.out.println("Выполнение было прервано. Причина:");
                System.out.println(ex.getMessage());
                break;
            }
        }
        System.out.printf("Итоговое состояние: \"%s\".\n",
                machineProcess.getLastConditionFromHistory());
    }

    public static void step(MachineProcessInterface machineProcess) {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Введите входной символ:
                """
        );

        String input = sc.nextLine();
        String currentCondition = machineProcess.getLastConditionFromHistory();
        try {
            machineProcess.step(input);

            System.out.println("\nШаг выполнен.");
            System.out.printf(
                    "Вход: \"%s\". Переход: \"%s\" -> \"%s\". Выход: \"%s\".",
                    input, currentCondition, machineProcess.getLastConditionFromHistory(),
                    machineProcess.getLastResult()
            );
        } catch (WrongMachineParams ex) {
            System.out.println();
            System.out.print(ex.getMessage());
            System.out.printf("Переход не произведен. Автомат остается в состоянии: \"%s\"\n",
                    currentCondition);
        }
    }

    public static void clear(MachineProcessInterface machineProcess) {
        System.out.printf(
                """
                
                ---------------------------------------------------------------
                
                %s возвращен в начальное состояние!
                """,
                machineProcess.getMachineName()
        );

        machineProcess.clearProcess();
    }

    public static void help() {
        System.out.println(HELP);
    }

    public static void reset() {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Параметры автомата сброшены!
                """
        );
    }

    public static void exit() {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Завершение работы приложения.
                Спасибо за использование "Симулятора Конечных Автоматов"!
                """
        );
    }

    public static void unknown() {
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Введена неизвестная команда!.
                Вы можете ознакомиться со списком доступных команд с помощью: /help.
                """
        );
    }

    public static void printCurrentStage(MachineProcessInterface machineProcess) {
        System.out.printf(
                """
                
                ---------------------------------------------------------------
                
                [%s | Состояние: %s | Последний выход: %s] >\s""",
                machineProcess.getMachineName(),
                machineProcess.getLastConditionFromHistory(),
                machineProcess.getLastResult()
        );
    }

    private final static String HELP =
            """
            
            ---------------------------------------------------------------
            
            Доступные команды:
            
                > /show   - вывести [конфигурацию автомата] / [текущие результаты моделирования]
                > /step   - выполнить один такт с заданным входным сигналом
                > /run    - выполнить несколько тактов подряд со списком входных сигналов
                > /clear  - вернуться в начальное состояние
                > /help   - вывести меню доступных команд
                > /reset  - сбросить автомат и ввести новые параметры
                > /save   - сохранить в файл [конфигурацию автомата] / [текущие результаты моделирования]
                > /exit   - выйти из приложения
            """;
}