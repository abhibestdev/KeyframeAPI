package me.abhi.keyframeapi.command;

import com.google.common.collect.Maps;
import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.command.parameter.*;
import me.abhi.keyframeapi.util.StringUtil;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.abhi.keyframeapi.command.framework.Command;
import me.abhi.keyframeapi.command.framework.CommandFramework;
import me.abhi.keyframeapi.command.framework.ParameterType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeyframeCommandHandler {

    private CommandFramework commandFramework;
    private List<Object> commands = new ArrayList<>();
    private Map<Class<?>, ParameterType<?>> parameterTypeMap = Maps.newHashMap();

    public KeyframeCommandHandler() {
        setupCommandFramework();

        //Register parameter types
        addParameterType(Boolean.class, new BooleanParameterType());
        addParameterType(Double.class, new DoubleParameterType());
        addParameterType(Float.class, new FloatParameterType());
        addParameterType(Integer.class, new IntegerParameterType());
        addParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        addParameterType(Player.class, new PlayerParameterType());
        addParameterType(String.class, new StringParameterType());
        addParameterType(GameMode.class, new GameModeParameterType());

    }

    private void setupCommandFramework() {
        commandFramework = new CommandFramework(KeyframeAPI.getInstance());
    }

    public void registerCommand(Object object) {
        commandFramework.registerCommands(object);
        commands.add(object);
    }

    public ParameterType<?> getParameterType(Class<?> clazz) {
        return parameterTypeMap.get(clazz);
    }

    public void addParameterType(Class<?> clazz, ParameterType<?> parameterType) {
        parameterTypeMap.put(clazz, parameterType);
    }

    public Object getCommand(String name) {
        for (Object o : commands) {
            for (Method method : o.getClass().getMethods()) {
                if (method.getAnnotation(Command.class) != null) {
                    Command command = method.getAnnotation(Command.class);
                    if (command.name().equalsIgnoreCase(name) || StringUtil.contains(command.aliases(), name)) {
                        return o;
                    }
                }
            }
        }
        return null;
    }

    public List<String> getAliases(Object o, String name) {
        List<String> aliases = new ArrayList<>();
        for (Method method : o.getClass().getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                Command command = method.getAnnotation(Command.class);

                if (command.name().equalsIgnoreCase(name) || StringUtil.contains(command.aliases(), name)) {

                    aliases.add(command.name().toLowerCase());
                    Arrays.stream(command.aliases()).forEach(a -> aliases.add(a.toLowerCase()));
                }
            }
        }
        return aliases;
    }

    public String getPermission(Object o, String name) {
        for (Method method : o.getClass().getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                Command command = method.getAnnotation(Command.class);

                if (command.name().equalsIgnoreCase(name) || StringUtil.contains(command.aliases(), name)) {
                    return command.permission();
                }
            }
        }
        return null;
    }
}
