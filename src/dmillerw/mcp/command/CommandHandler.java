package dmillerw.mcp.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class CommandHandler {

    private static Map<String, Command> commandMap = Maps.newHashMap();

    private static void registerCommand(Command command) {
        commandMap.put(command.getCommand(), command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias, command);
        }
    }

    static {
        registerCommand(new CommandQuit());
        registerCommand(new CommandSetBranch());
        registerCommand(new CommandGetType());
        registerCommand(new CommandGetMethod());
        registerCommand(new CommandGetField());
    }

    public static void registerCompleters(ConsoleReader consoleReader) {
        List<String> list = Lists.newArrayList();
        for (Command command : commandMap.values()) {
            list.add(command.getCommand());
            list.addAll(Arrays.asList(command.getAliases()));
        }
        consoleReader.addCompleter(new StringsCompleter(list));
    }

    public static Command getCommand(String[] args) {
        Command iCommand = commandMap.get(args[0]);
        if (iCommand != null) {
            return iCommand;
        } else {
            return null;
        }
    }
}
