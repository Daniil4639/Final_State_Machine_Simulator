package app.abstract_automaton_project.controllers;

import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.machines.MealyMachine;
import app.abstract_automaton_project.machines.MoorMachine;
import app.abstract_automaton_project.processes.MachineProcessInterface;
import app.abstract_automaton_project.processes.MealyProcess;
import app.abstract_automaton_project.processes.MoorProcess;
import app.abstract_automaton_project.utils.MachineFileTransformer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class SimulatorController {

    private MachineProcessInterface machineProcess;

    @FXML
    public void initialize() {
        clearResultsTable();

        inputSequenceField.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if (INPUTS_PATTERN.matcher(newText).matches()) {
                return change;
            } else {
                return null;
            }
        }));
    }

    public void setMachine(Machine newMachine) {
        if (newMachine instanceof MealyMachine mealyMachine) {
            machineProcess = new MealyProcess(mealyMachine);

            automatonTypeLabel.setText("Автомат Мили");
        } else {
            MoorMachine moorMachine = (MoorMachine) newMachine;
            machineProcess = new MoorProcess(moorMachine);

            automatonTypeLabel.setText("Автомат Мура");
        }

        clearResults();
    }

    public void showErrorMessage(String message) {
        Notifications.create()
                .title("Ошибка")
                .text(message)
                .hideAfter(Duration.seconds(5))
                .owner(rootPane.getScene().getWindow())
                .position(Pos.TOP_RIGHT)
                .darkStyle()
                .showError();
    }

    private void clearResultsTable() {
        if (machineProcess == null) {
            return;
        }

        resultsTable.getItems().clear();
        resultsTable.getColumns().clear();

        TableColumn<List<String>, String> namingColumn = new TableColumn<>("Такт");
        namingColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().get(0)));
        namingColumn.setSortable(false);
        namingColumn.setResizable(false);
        namingColumn.setPrefWidth(100);
        namingColumn.setStyle("-fx-alignment: CENTER;");
        resultsTable.getColumns().add(namingColumn);

        resultsTable.getColumns().add(getResultColumn(machineProcess.getTactNumber()));

        List<String> inputs = new ArrayList<>(List.of("Вход"));
        List<String> states = new ArrayList<>(List.of("Состояние"));
        states.add(machineProcess.getMachine().getStartCondition());
        List<String> outputs = new ArrayList<>(List.of("Выход"));
        if (machineProcess instanceof MoorProcess) {
            outputs.add("");
        }

        System.out.print(states);

        resultsTable.getItems().addAll(List.of(inputs, states, outputs));
    }

    private void addResultColumn() {
        int tact = machineProcess.getTactNumber();
        stepLabel.setText(String.valueOf(tact));
        currentStateLabel.setText(machineProcess.getLastConditionFromHistory());
        resultsTable.getColumns().add(getResultColumn(tact));

        resultsTable.getItems().get(0).add(machineProcess.getLastInput());
        resultsTable.getItems().get(1).add(machineProcess.getLastConditionFromHistory());
        resultsTable.getItems().get(2).add(machineProcess.getLastResult());

        successStepsLabel.setText(String.valueOf(
                Integer.parseInt(successStepsLabel.getText()) + 1
        ));
    }

    private TableColumn<List<String>, String> getResultColumn(int tact) {
        TableColumn<List<String>, String> tactColumn = new TableColumn<>(String.valueOf(tact));
        tactColumn.setCellValueFactory(
                param ->new SimpleStringProperty(
                        (param.getValue().size() <= tact) ? ("") : (param.getValue().get(tact))));
        tactColumn.setSortable(false);
        tactColumn.setResizable(true);
        tactColumn.setPrefWidth(100);
        tactColumn.setStyle("-fx-alignment: CENTER;");

        return tactColumn;
    }

    @FXML
    private boolean nextStep() {
        String inputs = inputSequenceField.getText();
        if (inputs.isEmpty()) {
            return false;
        }

        int firstInputBound = inputs.indexOf(',');
        if (firstInputBound == -1) firstInputBound = inputs.length();

        try {
            machineProcess.step(inputs.substring(0, firstInputBound));
            inputSequenceField.setText(
                    (firstInputBound != inputs.length()) ? inputs.substring(firstInputBound + 1) : "");

            addResultColumn();
        } catch (Exception ex) {
            errorStepsLabel.setText(String.valueOf(
                    Integer.parseInt(errorStepsLabel.getText()) + 1
            ));
            showErrorMessage(ex.getMessage());
            return false;
        }

        return true;
    }

    @FXML
    private void run() {
        while (true) {
            if (!nextStep()) {
                break;
            }
        }
    }

    @FXML
    private void exportResults() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "Text Files (*.txt)", "*.txt"
        );
        fileChooser.getExtensionFilters().add(extensionFilter);

        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        String lastDir = prefs.get("lastSaveDir", System.getProperty("user.home"));
        fileChooser.setInitialDirectory(new File(lastDir));

        fileChooser.setInitialFileName("results.txt");

        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            prefs.put("lastSaveDir", file.getParent());

            MachineFileTransformer.saveProcessResultsToFile(machineProcess, file.toPath());
        }
    }

    @FXML
    private void clearResults() {
        machineProcess.clearProcess();

        statesCountLabel.setText(String.valueOf(machineProcess.getMachine().getConditions().size()));
        currentStateLabel.setText(machineProcess.getMachine().getStartCondition());
        stepLabel.setText("1");
        successStepsLabel.setText("0");
        errorStepsLabel.setText("0");

        clearResultsTable();
    }

    @FXML
    private BorderPane rootPane;

    @FXML
    private TableView<List<String>> resultsTable;

    @FXML
    private Label automatonTypeLabel;

    @FXML
    private Label statesCountLabel;

    @FXML
    private Label currentStateLabel;

    @FXML
    private Label stepLabel;

    @FXML
    private Label successStepsLabel;

    @FXML
    private Label errorStepsLabel;

    @FXML
    private TextField inputSequenceField;

    private static final Pattern INPUTS_PATTERN = Pattern.compile("[a-zA-Z0-9_,]*");
}