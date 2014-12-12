package dmillerw.mcp.core;

import com.google.common.collect.Maps;
import dmillerw.mcp.MCPViewer;

import java.util.Map;

/**
 * @author dmillerw
 */
public class SimpleProfiler {

    private static Map<String, Long> profileMap = Maps.newHashMap();

    public static void start(String ident) {
        if (profileMap.containsKey(ident)) {
            throw new RuntimeException("Cannot start profiling " + ident + "! Already profiling!");
        }

        profileMap.put(ident, System.currentTimeMillis());
    }

    public static void stop(String ident) {
        if (!profileMap.containsKey(ident)) {
            throw new RuntimeException("Cannot stop profiling " + ident + "! Not profiling!");
        }

        long time = System.currentTimeMillis() - profileMap.get(ident);
        if (MCPViewer.debug) System.out.println("Profile '" + ident + "' took " + time + "ms");

        profileMap.remove(ident);
    }
}
