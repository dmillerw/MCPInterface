package dmillerw.mcp.command;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public abstract class Command {

    public abstract String getCommand();

    public String getWrongUsageString() {
        return "";
    }

    public String[] getAliases() {
        return new String[0];
    }

    public abstract boolean process(String[] args, PrintWriter out);

    @Override
    public int hashCode() {
        return getCommand().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Command))
            return false;

        Command command = (Command) obj;
        return getCommand().equals(command.getCommand());
    }
}
