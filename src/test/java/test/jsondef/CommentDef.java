package test.jsondef;

import java.util.HashMap;
import java.util.List;

public class CommentDef {
    private String name;
    private String label;
    private String type;
    private String placeholder;
    private String default_value;
    private boolean disable;
    private String order;
    private List<HashMap> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getDefault_value() {
        return default_value;
    }

    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<HashMap> getOptions() {
        return options;
    }

    public void setOptions(List<HashMap> options) {
        this.options = options;
    }
}
