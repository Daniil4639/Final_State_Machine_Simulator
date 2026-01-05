package app.abstract_automaton_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FSMSApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FSMSApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 950);
        stage.setTitle("Симулятор конечных автоматов");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}