package app.abstract_automaton_project.tables;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class TransitionModel {

    private final StringProperty input;

    private final Map<String, StringProperty> transitions;

    public TransitionModel(String input) {
        this.input = new SimpleStringProperty(input);
        this.transitions = new HashMap<>();
    }

    public StringProperty getInput() {
        return input;
    }

    public Map<String, StringProperty> getTransitions() {
        return transitions;
    }

    public void setTransition(String state, String value) {
        transitions.put(state, new SimpleStringProperty(value));
    }
}