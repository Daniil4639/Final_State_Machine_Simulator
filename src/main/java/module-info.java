module app.abstract_automaton_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.abstract_automaton_project to javafx.fxml;
    exports app.abstract_automaton_project;
}