package app.abstract_automaton_project.controllers;

import app.abstract_automaton_project.tables.OutputMealyModel;
import app.abstract_automaton_project.tables.TransitionModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DesignerController {

    @FXML
    public void initialize() {
        setupListView(statesListView);
        setupListView(inputsListView);
        setupListView(outputsListView);

        setupTextField(newStateField, NAME_PATTERN);
        setupTextField(inputsField, NAME_PATTERN);
        setupTextField(outputsField, MOOR_OUTPUT_PATTERN);

        statesListView.getItems().addListener((ListChangeListener<String>) change -> {
            initialStateCombo.getItems().clear();
            initialStateCombo.getItems().addAll(change.getList());

            updateTransitionsTable();
            updateOutputsTable();
        });

        inputsListView.getItems().addListener((ListChangeListener<String>) change -> {
            updateTransitionsTable();
            updateOutputsTable();
        });

        transitionTable.setEditable(true);
        outputTable.setEditable(true);
    }

    public void showErrorMessage(String message) {
        Notifications.create()
                .title("Ошибка")
                .text(message)
                .hideAfter(Duration.seconds(3))
                .owner(rootPane.getScene().getWindow())
                .position(Pos.TOP_RIGHT)
                .darkStyle()
                .showError();
    }

    @FXML
    private void onAutomatonTypeChanged() {
        String id = ((RadioButton) automatonTypeGroup.getSelectedToggle()).getId();
        if (id.equals("mealyRadio")) {
            mooreOutputSection.setVisible(false);
            mooreOutputSection.setManaged(false);

            mealyOutputTab.setDisable(false);
        } else {
            mooreOutputSection.setVisible(true);
            mooreOutputSection.setManaged(true);

            mealyOutputTab.setDisable(true);
        }
    }

    @FXML
    private void addState() {
        String statesStr = newStateField.getText().trim();
        if (statesStr.isEmpty()) {
            showErrorMessage("Поле состояния не должно быть пустым!");
            return;
        }

        List<String> states = Arrays.asList(statesStr.split(","));
        List<String> duplicates = findAllDuplicates(Stream.concat(states.stream(),
                statesListView.getItems().stream()).toList());

        if (!duplicates.isEmpty()) {
            showErrorMessage("Поля состояний должны быть уникальны: " + duplicates);
            return;
        }

        statesListView.getItems().addAll(states);
    }

    @FXML
    private void parseInputs() {
        String inputsStr = inputsField.getText().trim();
        if (inputsStr.isEmpty()) {
            showErrorMessage("Поле входных сигналов не должно быть пустым!");
            return;
        }

        List<String> inputs = Arrays.asList(inputsStr.split(","));
        List<String> duplicates = findAllDuplicates(Stream.concat(inputs.stream(),
                inputsListView.getItems().stream()).toList());

        if (!duplicates.isEmpty()) {
            showErrorMessage("Поля входных сигналов должны быть уникальны: " + duplicates);
            return;
        }

        inputsListView.getItems().addAll(inputs);
    }

    @FXML
    private void parseOutputs() {
        String outputsStr = outputsField.getText().trim();
        if (outputsStr.isEmpty()) {
            showErrorMessage("Поле выходных сигналов не должно быть пустым!");
            return;
        }

        List<String> outputs = Arrays.asList(outputsStr.split(","));
        List<String> duplicates = findAllDuplicates(Stream.concat(outputs.stream(),
                outputsListView.getItems().stream()).toList());

        if (!duplicates.isEmpty()) {
            showErrorMessage("Поля выходных сигналов должны быть уникальны: " + duplicates);
            return;
        }

        outputsListView.getItems().addAll(outputs);
    }

    @FXML
    private void refreshAll() {
        statesListView.getItems().clear();
        newStateField.setText("");

        inputsListView.getItems().clear();
        inputsField.setText("");

        outputsListView.getItems().clear();
        outputsField.setText("");

        initialStateCombo.getItems().clear();
    }

    @FXML
    private void validateAutomaton() {

    }

    @FXML
    private void goToSimulator() {

    }

    private void updateOutputsTable() {
        List<OutputMealyModel> outputMealyData = updateOutputsData();
        outputTable.getItems().clear();
        outputTable.getColumns().clear();

        TableColumn<OutputMealyModel, String> inputColumn = new TableColumn<>("Вход →");
        inputColumn.setCellValueFactory(
                param -> param.getValue().getInput());
        inputColumn.setSortable(false);
        inputColumn.setResizable(false);
        inputColumn.setPrefWidth(80);
        inputColumn.setStyle("-fx-alignment: CENTER;");

        outputTable.getColumns().add(inputColumn);

        for (String state: statesListView.getItems()) {
            TableColumn<OutputMealyModel, String> stateColumn = getOutputColumn(state, outputMealyData);

            outputTable.getColumns().add(stateColumn);
        }

        outputTable.getItems().addAll(outputMealyData);
    }

    private List<OutputMealyModel> updateOutputsData() {
        List<OutputMealyModel> outputsData = new ArrayList<>(List.copyOf(outputTable.getItems()));
        List<String> inputs = inputsListView.getItems();
        List<String> states = statesListView.getItems();

        outputsData.removeIf(row -> !inputs.contains(row.getInput().get()));

        List<String> currentInputs = outputsData.stream()
                .map(elem -> elem.getInput().get()).toList();
        for (String input: inputs) {
            if (!currentInputs.contains(input)) {
                outputsData.add(new OutputMealyModel(input));
            }
        }

        for (OutputMealyModel output: outputsData) {
            List<String> statesForRemove = new ArrayList<>();
            for (String state: output.getOutputs().keySet()) {
                if (!states.contains(state)) {
                    statesForRemove.add(state);
                }
            }
            statesForRemove.forEach(output.getOutputs()::remove);

            Set<String> currentStates = output.getOutputs().keySet();
            for (String state: states) {
                if (!currentStates.contains(state)) {
                    output.getOutputs().put(state, new SimpleStringProperty(""));
                }
            }
        }

        return outputsData;
    }

    private void updateTransitionsTable() {
        List<TransitionModel> transitionsData = updateTransitionsData();
        transitionTable.getItems().clear();
        transitionTable.getColumns().clear();

        TableColumn<TransitionModel, String> inputColumn = new TableColumn<>("Вход →");
        inputColumn.setCellValueFactory(
                param -> param.getValue().getInput());
        inputColumn.setSortable(false);
        inputColumn.setResizable(false);
        inputColumn.setPrefWidth(80);
        inputColumn.setStyle("-fx-alignment: CENTER;");

        transitionTable.getColumns().add(inputColumn);

        for (String state: statesListView.getItems()) {
            TableColumn<TransitionModel, String> stateColumn = getStateColumn(state, transitionsData);

            transitionTable.getColumns().add(stateColumn);
        }

        transitionTable.getItems().addAll(transitionsData);
    }

    private List<TransitionModel> updateTransitionsData() {
        List<TransitionModel> transitionsData = new ArrayList<>(List.copyOf(transitionTable.getItems()));
        List<String> inputs = inputsListView.getItems();
        List<String> states = statesListView.getItems();

        transitionsData.removeIf(row -> !inputs.contains(row.getInput().get()));

        List<String> currentInputs = transitionsData.stream()
                .map(elem -> elem.getInput().get()).toList();
        for (String input: inputs) {
            if (!currentInputs.contains(input)) {
                transitionsData.add(new TransitionModel(input));
            }
        }

        for (TransitionModel transition: transitionsData) {
            List<String> statesForRemove = new ArrayList<>();
            for (String state: transition.getTransitions().keySet()) {
                if (!states.contains(state)) {
                    statesForRemove.add(state);
                }
            }
            statesForRemove.forEach(transition.getTransitions()::remove);

            Set<String> currentStates = transition.getTransitions().keySet();
            for (String state: states) {
                if (!currentStates.contains(state)) {
                    transition.getTransitions().put(state, new SimpleStringProperty(""));
                }
            }
        }

        return transitionsData;
    }

    private TableColumn<OutputMealyModel, String> getOutputColumn(String state, List<OutputMealyModel> outputsData) {
        TableColumn<OutputMealyModel, String> outputColumn = new TableColumn<>(state);
        outputColumn.setPrefWidth(100);
        outputColumn.setSortable(false);
        outputColumn.setResizable(true);
        outputColumn.setStyle("-fx-alignment: CENTER;");

        outputColumn.setCellValueFactory(param -> {
            OutputMealyModel model = param.getValue();
            return model.getOutputs().get(state);
        });

        outputColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            String stateName = event.getTablePosition().getTableColumn().getText();
            String newValue = event.getNewValue();

            if (newValue == null || newValue.trim().isEmpty()) {
                showErrorMessage("Вводимый выходной сигнал не должен быть пустым!");
                outputTable.refresh();
                return;
            }

            if (!TABLE_CELL_PATTERN.matcher(newValue).matches()) {
                showErrorMessage("Вводимый выходной сигнал должен состоять из:\na-z A-Z 0-9 _ -");
                outputTable.refresh();
                return;
            }

            OutputMealyModel model = outputsData.get(row);
            model.setOutput(stateName, newValue);
        });

        outputColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        return outputColumn;
    }

    private TableColumn<TransitionModel, String> getStateColumn(String state, List<TransitionModel> transitionsData) {
        TableColumn<TransitionModel, String> stateColumn = new TableColumn<>(state);
        stateColumn.setPrefWidth(100);
        stateColumn.setSortable(false);
        stateColumn.setResizable(true);
        stateColumn.setStyle("-fx-alignment: CENTER;");

        stateColumn.setCellValueFactory(param -> {
            TransitionModel model = param.getValue();
            return model.getTransitions().get(state);
        });

        stateColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            String stateName = event.getTablePosition().getTableColumn().getText();
            String newValue = event.getNewValue();

            if (newValue == null || newValue.trim().isEmpty()) {
                showErrorMessage("Вводимое состояние не должно быть пустым!");
                transitionTable.refresh();
                return;
            }

            if (!TABLE_CELL_PATTERN.matcher(newValue).matches()) {
                showErrorMessage("Вводимое состояние должно состоять из:\na-z A-Z 0-9 _ -");
                transitionTable.refresh();
                return;
            }

            if (!newValue.equals("-") && !statesListView.getItems().contains(newValue)) {
                showErrorMessage("Введено неизвестное состояние!");
                transitionTable.refresh();
                return;
            }

            TransitionModel model = transitionsData.get(row);
            model.setTransition(stateName, newValue);
        });

        stateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        return stateColumn;
    }

    private void setupListView(ListView<String> view) {
        view.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String element, boolean empty) {
                super.updateItem(element, empty);

                if (empty || element == null) {
                    setText(null);
                    setContextMenu(null);
                }
                else {
                    setText(element);

                    ContextMenu menu = new ContextMenu();

                    MenuItem deleteItem = new MenuItem("Удалить");
                    deleteItem.getStyleClass().add("menu-item");
                    deleteItem.setOnAction(e -> deleteElement(element));
                    menu.getItems().add(deleteItem);

                    setContextMenu(menu);
                }
            }

            private void deleteElement(String element) {
                lv.getItems().remove(element);
            }
        });
    }

    private void setupTextField(TextField field, Pattern pattern) {
        field.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(newText).matches()) {
                return change;
            } else {
                return null;
            }
        }));
    }

    private List<String> findAllDuplicates(List<String> states) {
        Set<String> set = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String state: states) {
            if (state.equals("-")) {
                continue;
            }

            if (set.contains(state)) {
                duplicates.add(state);
            } else {
                set.add(state);
            }
        }

        return duplicates;
    }

    @FXML
    private BorderPane rootPane;

    @FXML
    private ToggleGroup automatonTypeGroup;

    @FXML
    private ListView<String> statesListView;

    @FXML
    private ListView<String> inputsListView;

    @FXML
    private ListView<String> outputsListView;

    @FXML
    private TextField newStateField;

    @FXML
    private TextField inputsField;

    @FXML
    private TextField outputsField;

    @FXML
    private ComboBox<String> initialStateCombo;

    @FXML
    private VBox mooreOutputSection;

    @FXML
    private TableView<TransitionModel> transitionTable;

    @FXML
    private Tab mealyOutputTab;

    @FXML
    private TableView<OutputMealyModel> outputTable;

    @FXML
    private Label statusLabel;

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_,]*");
    private static final Pattern MOOR_OUTPUT_PATTERN = Pattern.compile("[a-zA-Z0-9_,-]*");
    private static final Pattern TABLE_CELL_PATTERN = Pattern.compile("[a-zA-Z0-9_-]*");
}
