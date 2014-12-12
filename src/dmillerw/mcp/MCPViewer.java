package dmillerw.mcp;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import dmillerw.mcp.command.Command;
import dmillerw.mcp.command.CommandHandler;
import dmillerw.mcp.core.IOHelper;
import dmillerw.mcp.core.MappingLoader;
import dmillerw.mcp.core.SimpleProfiler;
import jline.console.ConsoleReader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class MCPViewer {

    private static final String METHODS = "https://raw.githubusercontent.com/MinecraftForge/FML/%s/conf/methods.csv";
    private static final String FIELDS = "https://raw.githubusercontent.com/MinecraftForge/FML/%s/conf/fields.csv";
    private static final String JOINED = "https://raw.githubusercontent.com/MinecraftForge/FML/%s/conf/joined.srg";

    public static String branch = "master";

    public static boolean debug = false;
    public static boolean quit = false;

    public static void main(String[] args) throws IOException {
        boolean shouldRefresh = false;
        boolean shouldCache = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("--refresh")) {
                shouldRefresh = true;
            } else if (arg.equalsIgnoreCase("--cache")) {
                shouldCache = true;
            } else if (arg.startsWith("--branch=")) {
                MCPViewer.branch = arg.replace("--branch=", "");
            } else if (arg.equalsIgnoreCase("--debug")) {
                MCPViewer.debug = true;
            }
        }

        refresh(shouldRefresh, shouldCache);

        try {
            inputLoop();
        } catch (IOException ex) {
            System.err.println("Failed to run inputLoop");
            if (debug) ex.printStackTrace();
        }
    }

    private static void inputLoop() throws IOException {
        ConsoleReader consoleReader = new ConsoleReader();
        consoleReader.setPrompt("prompt> ");
        consoleReader.setHistoryEnabled(true);

        CommandHandler.registerCompleters(consoleReader);

        String line;
        PrintWriter out = new PrintWriter(consoleReader.getOutput());

        while (!quit) {
            String[] selfArgs;
            while ((line = consoleReader.readLine()) != null) {
                if (quit)
                    break;

                selfArgs = line.split(" ");
                if (selfArgs.length > 0) {
                    Command command = CommandHandler.getCommand(selfArgs);
                    if (command != null) {
                        if (!command.process(selfArgs, out)) {
                            out.println("Invalid Usage: " + command.getWrongUsageString());
                        }
                    } else {
                        out.println("Invalid Command: " + selfArgs[0]);
                    }
                    out.flush();
                }
            }
        }
    }

    public static void refresh(boolean download, boolean cache) {
        // Original mapping files
        File methods = new File("mapping/methods.csv");
        File fields = new File("mapping/fields.csv");
        File joined = new File("mapping/joined.srg");

        // MD5 hashes of mapping files
        File md5Storage = new File("cache/md5.cache");

        // Cached mapping values
        File typeCache = new File("cache/type.cache");
        File methodCache = new File("cache/method.cache");
        File fieldCache = new File("cache/field.cache");

        // Re-download the mappings if one or more don't exist
        download |= !(methods.exists() || fields.exists() || joined.exists());

        // Re-cache if one or more of the cache files don't exist, or the md5 data doesn't exist, or we're re-downloading
        cache |= !(md5Storage.exists() && typeCache.exists() && methodCache.exists() && fieldCache.exists());

        if (download) {
            SimpleProfiler.start("Mapping Download");
            File dir = new File("mapping");
            if (!dir.exists())
                dir.mkdirs();

            replace(methods, METHODS.replace("%s", MCPViewer.branch));
            replace(fields, FIELDS.replace("%s", MCPViewer.branch));
            replace(joined, JOINED.replace("%s", MCPViewer.branch));
            SimpleProfiler.stop("Mapping Download");
        }

        // If we haven't already been told to cache either by re-downloading, or
        // files not existing in the first place, check the md5 data, as we already
        // have mappings
        if (!cache) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(md5Storage));
                if (!IOHelper.getFileMD5(methods).equals(properties.getProperty("methods.csv", "NULL"))) {
                    cache = true;
                }
                if (!IOHelper.getFileMD5(fields).equals(properties.getProperty("fields.csv", "NULL"))) {
                    cache = true;
                }
                if (!IOHelper.getFileMD5(joined).equals(properties.getProperty("joined.srg", "NULL"))) {
                    cache = true;
                }
            } catch (IOException ex) {
                cache = true;
            }
        }

        MappingLoader.types = Sets.newHashSet();
        MappingLoader.methods = Sets.newHashSet();
        MappingLoader.fields = Sets.newHashSet();

        if (!cache) {
            System.out.println("Reading mapping data from cache");

            SimpleProfiler.start("Read Cache");

            try {
                IOHelper.readTypesFromFile(typeCache);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                IOHelper.readMethodsFromFile(methodCache);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                IOHelper.readFieldsFromFile(fieldCache);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            SimpleProfiler.stop("Read Cache");
        } else {
            // If we're caching, re-parse the mapping files for a clean and up to date cache
            SimpleProfiler.start("Parse Files");
            MappingLoader.parseFiles(methods, fields, joined);
            SimpleProfiler.stop("Parse Files");
        }

        if (cache) {
            System.out.println("Writing mapping data to cache");

            SimpleProfiler.start("Write Cache");
            try {
                File dir = new File("cache");
                if (!dir.exists())
                    dir.mkdirs();

                typeCache.delete();
                methodCache.delete();
                fieldCache.delete();
                md5Storage.delete();

                typeCache.createNewFile();
                methodCache.createNewFile();
                fieldCache.createNewFile();
                md5Storage.createNewFile();

                try {
                    IOHelper.writeTypesToFile(typeCache);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    IOHelper.writeMethodsToFile(methodCache);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    IOHelper.writeFieldsToFile(fieldCache);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Properties properties = new Properties();
                properties.setProperty("methods.csv", IOHelper.getFileMD5(methods));
                properties.setProperty("fields.csv", IOHelper.getFileMD5(fields));
                properties.setProperty("joined.srg", IOHelper.getFileMD5(joined));
                properties.store(new FileOutputStream(md5Storage), "DO NOT MODIFY");
            } catch (IOException ex) {
                if (debug) ex.printStackTrace();
            }
            SimpleProfiler.stop("Write Cache");
        }
    }

    private static void replace(File file, String path) {
        try {


            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(ByteStreams.toByteArray(urlConnection.getInputStream()));

            System.out.println("Refreshed " + file.getName());

            fileOutputStream.close();
        } catch (IOException ex) {
            System.err.println("Failed to refresh " + file.getName());
            if (debug) ex.printStackTrace();
        }
    }
}
