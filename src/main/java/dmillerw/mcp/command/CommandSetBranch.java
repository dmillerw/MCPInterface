package dmillerw.mcp.command;

import dmillerw.mcp.MCPViewer;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public class CommandSetBranch extends Command {

    @Override
    public String getCommand() {
        return "branch";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"set-branch"};
    }

    @Override
    public String getWrongUsageString() {
        return getCommand() + " branch";
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        if (args.length == 2) {
            out.println("Branch set to '" + args[1] + "'");
            MCPViewer.branch = args[1];
            MCPViewer.refresh(true, true);
            return true;
        } else {
            return false;
        }
    }
}
