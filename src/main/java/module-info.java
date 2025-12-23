module app.abstract_automaton_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.abstract_automaton_project to javafx.fxml;
    exports app.abstract_automaton_project;
    exports app.abstract_automaton_project.console;
    exports app.abstract_automaton_project.exceptions;
    exports app.abstract_automaton_project.machines;
    exports app.abstract_automaton_project.processes;
    exports app.abstract_automaton_project.utils;
    opens app.abstract_automaton_project.console to javafx.fxml;
}