package dmillerw.mcp.command;

import dmillerw.mcp.core.MappingLoader;
import dmillerw.mcp.mapping.TypeMapping;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public class CommandGetType extends Command {

    @Override
    public String getCommand() {
        return "get-type";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"gt"};
    }

    @Override
    public String getWrongUsageString() {
        return getCommand() + " class";
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        if (args.length == 2) {
            TypeMapping typeMapping = MappingLoader.getType(args[1]);
            if (typeMapping == null) {
                out.println("Failed to find class '" + args[1] + "'");
            } else {
                out.println(typeMapping);
            }
            return true;
        } else {
            return false;
        }
    }
}
