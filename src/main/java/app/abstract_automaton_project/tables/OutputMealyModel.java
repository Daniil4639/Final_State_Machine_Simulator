package app.abstract_automaton_project.tables;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class OutputMealyModel {

    private final StringProperty input;

    private final Map<String, StringProperty> outputs;

    public OutputMealyModel(String input) {
        this.input = new SimpleStringProperty(input);
        this.outputs = new HashMap<>();
    }

    public StringProperty getInput() {
        return input;
    }

    public Map<String, StringProperty> getOutputs() {
        return outputs;
    }

    public void setOutput(String state, String value) {
        outputs.put(state, new SimpleStringProperty(value));
    }
}