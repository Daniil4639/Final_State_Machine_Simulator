module app.abstract_automaton_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.controlsfx.controls;
    requires java.prefs;


    opens app.abstract_automaton_project to javafx.fxml;
    exports app.abstract_automaton_project;
    exports app.abstract_automaton_project.exceptions;
    exports app.abstract_automaton_project.machines;
    exports app.abstract_automaton_project.processes;
    exports app.abstract_automaton_project.utils;
    exports app.abstract_automaton_project.controllers;
    opens app.abstract_automaton_project.controllers to javafx.fxml;
}