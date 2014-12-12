package dmillerw.mcp.command;

import dmillerw.mcp.mapping.FieldMapping;
import dmillerw.mcp.core.MappingLoader;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public class CommandGetField extends Command {

    @Override
    public String getCommand() {
        return "get-field";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"gf"};
    }

    @Override
    public String getWrongUsageString() {
        return getCommand() + " class.field";
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        String type;
        String field;

        if (args.length != 2) {
            return false;
        }

        if (!(args[1].contains("."))) {
            return false;
        }

        String[] search = args[1].replace(".", "/").split("/"); // Because split takes Regex
        type = search[0];
        field = search[1];

        FieldMapping fieldMapping = MappingLoader.getField(type, field);
        if (fieldMapping != null) {
            for (String string : fieldMapping.toPrettyString()) {
                out.println(string);
            }
        } else {
            out.println("Failed to find '" + field + "' in '" + type + "'");
        }

        return true;
    }
}
