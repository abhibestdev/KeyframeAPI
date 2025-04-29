package me.abhi.keyframeapi.command.framework;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParameterData implements Data {

    private String name;
    private String defaultValue;
    private Class<?> type;
    private int methodIndex;
    private Class<? extends ParameterType<?>> parameterType;
}
