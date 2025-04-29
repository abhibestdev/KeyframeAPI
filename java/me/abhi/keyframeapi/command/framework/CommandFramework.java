package me.abhi.keyframeapi.command.framework;

import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.util.StringUtil;
import me.abhi.keyframeapi.util.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

/**
 * Command Framework - CommandFramework <br>
 * The main command framework class used for controlling the framework.
 *
 * @author minnymin3
 */
public class CommandFramework implements CommandExecutor {

    private Map<String, Entry<Method, Object>> commandMap = new HashMap<String, Entry<Method, Object>>();
    private CommandMap map;
    private Plugin plugin;

    /**
     * Initializes the command framework and sets up the command maps
     */
    public CommandFramework(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        return handleCommand(sender, cmd, label, args);
    }

    /**
     * Handles commands. Used in the onCommand method in your JavaPlugin class
     * `
     *
     * @param sender The {@link CommandSender} parsed from
     *               onCommand
     * @param cmd    The {@link org.bukkit.command.Command} parsed from onCommand
     * @param label  The label parsed from onCommand
     * @param args   The arguments parsed from onCommand
     * @return Always returns true for simplicity's sake in onCommand
     */
    public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; x++) {
                buffer.append("." + args[x].toLowerCase());
            }
            String cmdLabel = buffer.toString();
            if (commandMap.containsKey(cmdLabel)) {
                Method method = commandMap.get(cmdLabel).getKey();
                Object methodObject = commandMap.get(cmdLabel).getValue();
                Command command = method.getAnnotation(Command.class);
                if (!command.permission().equalsIgnoreCase("") && ((command.permission().equalsIgnoreCase("op") && !sender.isOp()) || (!command.permission().equalsIgnoreCase("op") && !sender.hasPermission(command.permission())))) {
                    sender.sendMessage(ChatColor.RED + command.noPerm());
                    return true;
                }
                if (command.inGameOnly() && !(sender instanceof Player)) {
                    sender.sendMessage("This command is only performable in game");
                    return true;
                }

                int subCommand = cmdLabel.split("\\.").length - 1;
                String[] modArgs = new String[args.length - subCommand];
                for (int j = 0; j < args.length - subCommand; j++) {
                    modArgs[j] = args[j + subCommand];
                }

                String subCommandName = null;
                try {
                    subCommandName = cmdLabel.split("\\.")[1];
                } catch (Exception ex) {

                }

                if (command.runAsync()) {
                    String finalSubCommandName = subCommandName;
                    ThreadUtil.runAsync(() -> {
                        invokeCommand(method, methodObject, sender, label, finalSubCommandName, modArgs);
                    });
                    return true;
                } else {
                    invokeCommand(method, methodObject, sender, label, subCommandName, modArgs);
                    return true;
                }
            }
        }
        defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
        return true;
    }


    private boolean invokeCommand(Method method, Object methodObject, CommandSender sender, String cmdLabel, String subCommand, String[] args) {
        try {
            List<Object> objects = new ArrayList<>(method.getParameterCount());
            List<Param> params = new ArrayList<>(method.getParameterCount() - 1);
            int requiredParams = 0;

            for (int j = 1; j < method.getParameterCount(); j++) {
                Parameter p = method.getParameters()[j];
                if (p.isAnnotationPresent(Param.class)) {
                    Param param = p.getAnnotation(Param.class);

                    params.add(param);
                    if (!param.optional()) requiredParams += 1;
                } else if (p.isAnnotationPresent(Flag.class)) {
                    Flag flag = p.getAnnotation(Flag.class);

                    //  params.add(flag);
                }
            }

            if (args.length < requiredParams) {
                StringBuilder usageMessage = new StringBuilder(ChatColor.RED + "Usage: /" + cmdLabel + " " + (subCommand != null ? subCommand + " " : ""));
                params.forEach(p -> {
                    if (!p.optional())
                        usageMessage.append("<" + p.name() + (p.wildcard() ? "..." : "") + "> ");
                    else usageMessage.append("[" + p.name() + (p.wildcard() ? "..." : "") + "] ");

                });

                sender.sendMessage(usageMessage.toString());
                return true;
            }

            objects.add(0, sender);

            int readingArg = 0;
            boolean passed = false;
            for (int j = 1; j < method.getParameterCount(); j++) {
                Parameter p = method.getParameters()[j];

                if (p.isAnnotationPresent(Param.class)) {
                    Param param = p.getAnnotation(Param.class);
                    params.add(param);

                    String argument = "";
                    if (param.wildcard()) {
                        argument = StringUtil.join(Arrays.asList(args), readingArg);

                        if (argument.isEmpty()) {
                            argument = param.defaultValue();
                        }
                        objects.add(j, argument);
                    } else {
                        ParameterType<?> type = KeyframeAPI.getInstance().getKeyframeCommandHandler().getParameterType(p.getType());

                        Object o = type.transform(sender, args.length > readingArg || passed ? args[readingArg] : param.defaultValue());
                        passed = false;
                        if (o == null) {
                            return false;
                        }
                        objects.add(j, o);
                    }
                    //System.out.println("Reading arg " + readingArg + " [" + arg + "] " + param.name());
                } else if (p.isAnnotationPresent(Flag.class)) {
                    Flag flag = p.getAnnotation(Flag.class);

                    try {
                        Matcher matcher = Flag.FLAG_PATTERN.matcher(args[readingArg]);
                        if (matcher.find() && matchesFlag(flag.value(), matcher.group())) {
                            System.out.println(matcher.group());
                            objects.add(j, !flag.defaultValue());
                        } else {
                            objects.add(j, flag.defaultValue());
                            passed = true;
                            readingArg -= 1;
                        }
                    } catch (Exception ex) {
                        objects.add(j, flag.defaultValue());
                    }
                }
                readingArg += 1;
            }

            method.invoke(methodObject, objects.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean matchesFlag(String[] values, String group) {
        group = group.replace("-", "");

        boolean containsFlag = false;
        for (String value : values) {
            if (value.equalsIgnoreCase(group)) {
                containsFlag = true;
            }
        }
        return containsFlag;
    }

    /**
     * Registers all command and completer methods inside of the object. Similar
     * to Bukkit's registerEvents method.
     *
     * @param obj The object to register the commands of
     */
    public void registerCommands(Object obj) {
        for (Method m : obj.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                Command command = m.getAnnotation(Command.class);
                if (m.getParameterTypes()[0] != CommandSender.class) {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                    continue;
                }
                registerCommand(command, command.name(), m, obj);
                for (String alias : command.aliases()) {
                    registerCommand(command, alias, m, obj);
                }
            } else if (m.getAnnotation(Completer.class) != null) {
                Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes().length == 0
                        || m.getParameterTypes()[0] != CommandArgs.class) {
                    System.out.println("Unable to register tab completer " + m.getName()
                            + ". Unexpected method arguments");
                    continue;
                }
                if (m.getReturnType() != List.class) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    continue;
                }
                registerCompleter(comp.name(), m, obj);
                for (String alias : comp.aliases()) {
                    registerCompleter(alias, m, obj);
                }
            }
        }
    }

    /**
     * Registers all the commands under the plugin's help
     */
    public void registerHelp() {
        Set<HelpTopic> help = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());
        for (String s : commandMap.keySet()) {
            if (!s.contains(".")) {
                org.bukkit.command.Command cmd = map.getCommand(s);
                HelpTopic topic = new GenericCommandHelpTopic(cmd);
                help.add(topic);
            }
        }
        IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help,
                "Below is a list of all " + plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

    public void registerCommand(Command command, String label, Method m, Object obj) {
        commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<Method, Object>(m, obj));
        commandMap.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry<Method, Object>(m, obj));
        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (map.getCommand(cmdLabel) != null) {
            map.getKnownCommands().remove(cmdLabel);
        }
        org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, this, plugin);
        map.register(plugin.getName(), cmd);

        if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
            map.getCommand(cmdLabel).setDescription(command.description());
        }
        if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
            map.getCommand(cmdLabel).setUsage(command.usage());
        }
    }

    public void registerCompleter(String label, Method m, Object obj) {
        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (map.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command command = new BukkitCommand(cmdLabel, this, plugin);
            map.register(plugin.getName(), command);
        }
        if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
            BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);
            if (command.completer == null) {
                command.completer = new BukkitCompleter();
            }
            command.completer.addCompleter(label, m, obj);
        } else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                Object command = map.getCommand(cmdLabel);
                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command) == null) {
                    BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(command, completer);
                } else if (field.get(command) instanceof BukkitCompleter) {
                    BukkitCompleter completer = (BukkitCompleter) field.get(command);
                    completer.addCompleter(label, m, obj);
                } else {
                    System.out.println("Unable to register tab completer " + m.getName()
                            + ". A tab completer is already registered for that command!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void defaultCommand(CommandArgs args) {
        args.getSender().sendMessage(args.getLabel() + " is not handled! Oh noes!");
    }

}
