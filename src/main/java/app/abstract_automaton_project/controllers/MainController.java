package app.abstract_automaton_project.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainController {

    private static final String REP_URL = "https://github.com/Daniil4639/Final_State_Machine_Simulator";

    @FXML
    private StackPane contentArea;

    @FXML
    private Pane designerPane;

    @FXML
    private Pane simulatorPane;

    @FXML
    private Button designerNavButton;

    @FXML
    private Button simulatorNavButton;

    public void initialize() {
        setActivePane(designerPane, designerNavButton);
    }

    @FXML
    private void switchToDesigner() {
        setActivePane(designerPane, designerNavButton);
    }

    @FXML
    private void switchToSimulator() {
        setActivePane(simulatorPane, simulatorNavButton);
    }

    @FXML
    private void showAboutDialog() throws URISyntaxException, IOException {
        Desktop.getDesktop()
                .browse(new URI(REP_URL));
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
}