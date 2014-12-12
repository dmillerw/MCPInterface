package dmillerw.mcp.command;

import dmillerw.mcp.core.MappingLoader;
import dmillerw.mcp.mapping.MethodMapping;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public class CommandGetMethod extends Command {

    @Override
    public String getCommand() {
        return "get-method";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"gm"};
    }

    @Override
    public String getWrongUsageString() {
        return getCommand() + " class.method [desc]";
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        String type;
        String method;
        String desc = "";

        if (args.length < 2 || args.length > 3) {
            return false;
        }

        if (!(args[1].contains("."))) {
            return false;
        }

        if (args.length == 2) {
            String[] search = args[1].replace(".", "/").split("/"); // Because split takes Regex
            type = search[0];
            method = search[1];
        } else if (args.length == 3) {
            String[] search = args[1].replace(".", "/").split("/"); // Because split takes Regex
            type = search[0];
            method = search[1];
            desc = args[2];
        } else {
            return false;
        }

        MethodMapping[] methods = MappingLoader.getMethods(type, method, desc);
        if (methods == null || methods.length == 0) {
            out.println("Failed to find '" + method + "' in '" + type + "'");
        } else {
            if (methods.length > 1) {
                out.println("We found multiple methods that matched your search:");
                for (MethodMapping methodMapping : methods) {
                    for (String string : methodMapping.toPrettyString()) {
                        out.println(" * " + string);
                    }
                    out.println(" --- ");
                }
            } else {
                for (String string : methods[0].toPrettyString()) {
                    out.println(string);
                }
            }
        }

        return true;
    }
}
