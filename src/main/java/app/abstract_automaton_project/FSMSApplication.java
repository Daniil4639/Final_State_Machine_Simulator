package app.abstract_automaton_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FSMSApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(FSMSApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(mainLoader.load(), 1280, 950);
        stage.setTitle("Симулятор конечных автоматов");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(FSMSApplication.class.getResourceAsStream("logo.png"))
        ));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}