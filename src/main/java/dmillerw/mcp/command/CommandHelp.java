package dmillerw.mcp.command;

import com.google.common.collect.Sets;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

/**
 * @author dmillerw
 */
public class CommandHelp extends Command {


    public String getCommand() {
        return "help";
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        if (args.length != 1)
            return false;

        Set<Command> commandSet = Sets.newHashSet();
        for (Command command : CommandHandler.getCommands()) {
            commandSet.add(command);
        }

        for (Command command : commandSet) {
            out.println(command.getCommand() + " " + Arrays.toString(command.getAliases()) + " : " + command.getWrongUsageString());
        }

        return true;
    }
}
