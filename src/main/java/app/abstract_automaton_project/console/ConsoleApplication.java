package app.abstract_automaton_project.console;

import app.abstract_automaton_project.exceptions.ReadMachineException;
import app.abstract_automaton_project.machines.MealyMachine;
import app.abstract_automaton_project.machines.MoorMachine;
import app.abstract_automaton_project.processes.MachineProcessInterface;
import app.abstract_automaton_project.processes.MealyProcess;
import app.abstract_automaton_project.processes.MoorProcess;
import app.abstract_automaton_project.utils.MachineFileTransformer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
                case "/step" -> ConsoleCommands.step(machineProcess);
                case "/run" -> ConsoleCommands.run(machineProcess);
                case "/clear" -> ConsoleCommands.clear(machineProcess);
                case "/help" -> ConsoleCommands.help();
                case "/reset" -> {
                    ConsoleCommands.reset();
                    machineProcess = createProcess();
                    ConsoleCommands.help();
                }
                case "/save" -> ConsoleCommands.save(machineProcess);
                case "/exit" -> {
                    ConsoleCommands.exit();
                    isRunning = false;
                }
                default -> ConsoleCommands.unknown();
            }
        }
    }

    private static MachineProcessInterface createProcess() {
        while (true) {
            int machineNum = getMachineNum();

            if (machineNum == 3) {
                System.out.print("\n---------------------------------------------------------------\n");
                Path filePath = getFilePathToOpen();

                try {
                    return MachineFileTransformer.getMachineFromFile(filePath);
                } catch (ReadMachineException ex) {
                    System.out.printf("Ошибка в процессе чтения состояния автомата: %s%n",
                            ex.getMessage());
                    continue;
                }
            }

            System.out.printf(
                    """
                            
                            ---------------------------------------------------------------
                            
                            Выбран автомат %s
                            
                            Необходимо задать его параметры.
                            """
                    , (machineNum == 1) ? ("Мили") : ("Мура"));

            return (machineNum == 1) ? (createMealyProcess()) : (createMoorProcess());
        }
    }

    private static Path getFilePathToOpen() {
        while (true) {
            System.out.println("Введите путь до файла с сохраненной конфигурацией автомата:");
            Path filePath = Paths.get(sc.nextLine());
            if (!Files.exists(filePath)) {
                System.out.printf("%nФайл по пути \"%s\" не найден!%n", filePath);
                continue;
            }

            return filePath;
        }
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
        System.out.print(
                """
                
                ---------------------------------------------------------------
                
                Выберите тип автомата для работы:
                    [1] - Автомат Мили
                    [2] - Автомат Мура
                    [3] - Из файла
                
                Ваш выбор [1/2/3]:
                """
        );
        int machineNum;

        while (true) {
            String symbol = sc.nextLine();
            try {
                machineNum = Integer.parseInt(symbol);
            } catch (NumberFormatException ex) {
                System.out.println("Не удалось распознать номер. Повторите [1/2/3]:");
                continue;
            }

            if (!List.of(1, 2, 3).contains(machineNum)) {
                System.out.println("Введен некорректный номер. Повторите [1/2/3]:");
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
                """;
}