package me.abhi.keyframeapi.command.framework;

import java.beans.ConstructorProperties;
import java.util.List;

public class FlagData implements Data {
    private List<String> names;
    private String description;
    private boolean defaultValue;
    private int methodIndex;

    @ConstructorProperties({"names", "description", "defaultValue", "methodIndex"})
    public FlagData(final List<String> names, final String description, final boolean defaultValue, final int methodIndex) {
        this.names = names;
        this.description = description;
        this.defaultValue = defaultValue;
    }
}