package dmillerw.mcp.command;

import dmillerw.mcp.MCPViewer;

import java.io.PrintWriter;

/**
 * @author dmillerw
 */
public class CommandQuit extends Command {

    @Override
    public String getCommand() {
        return "quit";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"exit"};
    }

    @Override
    public boolean process(String[] args, PrintWriter out) {
        MCPViewer.quit = true;
        return true;
    }
}
