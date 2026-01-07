package app.abstract_automaton_project.controllers;

import app.abstract_automaton_project.machines.Machine;
import app.abstract_automaton_project.utils.MachineFileTransformer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

public class MainController {

    private static final String REP_URL = "https://github.com/Daniil4639/Final_State_Machine_Simulator";

    @FXML
    private VBox anchorBox;

    @FXML
    private StackPane contentArea;

    @FXML
    private Pane designerPane;

    @FXML
    private DesignerController designerPaneController;

    @FXML
    private SimulatorController simulatorPaneController;

    @FXML
    private Pane simulatorPane;

    @FXML
    private Button designerNavButton;

    @FXML
    private Button simulatorNavButton;

    @FXML
    public void initialize() {
        designerPaneController.setSimulatorController(simulatorPaneController);
        setActivePane(designerPane, designerNavButton);
    }

    @FXML
    private void switchToDesigner() {
        setActivePane(designerPane, designerNavButton);
    }

    @FXML
    private void switchToSimulator() {
        if (designerPaneController.getMachine() == null) {
            designerPaneController.showErrorMessage(
                    """
                    Автомат не готов к работе!
                    Нажмите на кнопку "Проверить корректность" или исправьте недочеты.
                    """
            );
            return;
        }

        setActivePane(simulatorPane, simulatorNavButton);
    }

    @FXML
    private void showAboutDialog() throws URISyntaxException, IOException {
        Desktop.getDesktop()
                .browse(new URI(REP_URL));
    }

    @FXML
    private void createNewAutomaton() {
        designerPaneController.refreshAll();
        setActivePane(designerPane, designerNavButton);
    }

    @FXML
    private void openAutomaton() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "FSMS Files (*.fsms)", "*.fsms"
        );
        fileChooser.getExtensionFilters().add(extensionFilter);

        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        String lastDir = prefs.get("lastSaveDir", System.getProperty("user.home"));
        fileChooser.setInitialDirectory(new File(lastDir));

        fileChooser.setInitialFileName("machine.fsms");

        File file = fileChooser.showOpenDialog(anchorBox.getScene().getWindow());

        if (file != null) {
            try {
                prefs.put("lastOpenDir", file.getParent());

                designerPaneController.setMachineProcess(
                        MachineFileTransformer.getMachineFromFile(file.toPath()));
            } catch (Exception ex) {
                designerPaneController.showErrorMessage(ex.getMessage());
            }
        }
    }

    @FXML
    private void saveAutomaton() {
        Machine machine = designerPaneController.getMachine();
        if (machine == null) {
            designerPaneController.showErrorMessage(
                    """
                    Для сохранения автомат должен быть готов к работе!
                    Нажмите на кнопку "Проверить корректность" или исправьте недочеты.
                    """
            );
            return;
        }

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "FSMS Files (*.fsms)", "*.fsms"
        );
        fileChooser.getExtensionFilters().add(extensionFilter);

        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        String lastDir = prefs.get("lastSaveDir", System.getProperty("user.home"));
        fileChooser.setInitialDirectory(new File(lastDir));

        fileChooser.setInitialFileName("machine.fsms");

        File file = fileChooser.showSaveDialog(anchorBox.getScene().getWindow());

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".fsms")) {
                file = new File(file.getAbsolutePath() + ".fsms");
            }

            prefs.put("lastSaveDir", file.getParent());

            saveToFile(file, machine);
        }
    }

    @FXML
    private void closeApp() {
        Platform.exit();
    }

    private void setActivePane(Pane paneToShow, Button activeButton) {
        for (Node node : contentArea.getChildren()) {
            node.setVisible(false);
        }

        paneToShow.setVisible(true);

        designerNavButton.getStyleClass().remove("active");
        simulatorNavButton.getStyleClass().remove("active");
        activeButton.getStyleClass().add("active");
    }

    private void saveToFile(File file, Machine machine) {
        MachineFileTransformer.saveMachineToFIle(
                machine,
                file.toPath()
        );
    }
}