package app.abstract_automaton_project.console;

import app.abstract_automaton_project.machines.MealyMachine;
import app.abstract_automaton_project.machines.MoorMachine;
import app.abstract_automaton_project.processes.MachineProcessInterface;
import app.abstract_automaton_project.processes.MealyProcess;
import app.abstract_automaton_project.processes.MoorProcess;

import java.util.Scanner;

public class ConsoleApplication {

    private final static Scanner sc = new Scanner(System.in);

    private static boolean isRunning = true;

    public static void main(String[] args) {
        System.out.print(GREETINGS);

        MachineProcessInterface machineProcess = createProcess();

        ConsoleCommands.help();

        while (isRunning) {
            ConsoleCommands.printCurrentStage(machineProcess);

            String nextCommand = sc.nextLine();
            switch (nextCommand.replace(" ", "")) {
                case "/show" -> ConsoleCommands.show(machineProcess);
                case "/run" -> ConsoleCommands.run(machineProcess);
                case "/step" -> ConsoleCommands.step(machineProcess);
                case "/clear" -> ConsoleCommands.clear(machineProcess);
                case "/help" -> ConsoleCommands.help();
                case "/reset" -> {
                    ConsoleCommands.reset();
                    machineProcess = createProcess();
                    ConsoleCommands.help();
                }
                case "/exit" -> {
                    ConsoleCommands.exit();
                    isRunning = false;
                }
                default -> ConsoleCommands.unknown();
            }
        }
    }

    private static MachineProcessInterface createProcess() {
        int machineNum = getMachineNum();

        System.out.printf(
                """
                
                ---------------------------------------------------------------
                
                Выбран автомат %s
                
                Необходимо задать его параметры.
                """
                , (machineNum == 1) ? ("Мили") : ("Мура"));

        return (machineNum == 1) ? (createMealyProcess()) : (createMoorProcess());
    }

    private static MachineProcessInterface createMealyProcess() {
        MealyMachine mealyMachine = MealyMachine.createMealyMachineConsole();

        return new MealyProcess(mealyMachine);
    }

    private static MachineProcessInterface createMoorProcess() {
        MoorMachine moorMachine = MoorMachine.createMoorMachineConsole();

        return new MoorProcess(moorMachine);
    }

    private static int getMachineNum() {
        int machineNum;

        while (true) {
            String symbol = sc.nextLine();
            try {
                machineNum = Integer.parseInt(symbol);
            } catch (NumberFormatException ex) {
                System.out.println("Не удалось распознать номер. Повторите [1/2]:");
                continue;
            }

            if (machineNum != 1 && machineNum != 2) {
                System.out.println("Введен некорректный номер. Повторите [1/2]:");
            } else {
                break;
            }
        }

        return machineNum;
    }

    private final static String GREETINGS =
                """
                ===============================================================
                ||                Симулятор Конечных Автоматов               ||
                ||                        (Мили и Мура)                      ||
                ===============================================================
                
                Добро пожаловать в симулятор конечных автоматов!
                
                ---------------------------------------------------------------
                
                Выберите тип автомата для работы:
                    [1] - Автомат Мили
                    [2] - Автомат Мура
                
                Ваш выбор [1/2]:
                """;
}