package dmillerw.mcp.core;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import dmillerw.mcp.MCPViewer;
import dmillerw.mcp.mapping.FieldMapping;
import dmillerw.mcp.mapping.MethodMapping;
import dmillerw.mcp.mapping.TypeMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * @author dmillerw
 */
public class MappingLoader {

    public static Set<TypeMapping> types;
    public static Set<MethodMapping> methods;
    public static Set<FieldMapping> fields;

    private static MethodMapping internal_getMethod(String srg) {
        for (MethodMapping methodMapping : methods) {
            if (methodMapping.srg.equals(srg)) {
                return methodMapping;
            }
        }
        return null;
    }

    private static FieldMapping internal_getField(String srg) {
        for (FieldMapping fieldMapping : fields) {
            if (fieldMapping.srg.equals(srg)) {
                return fieldMapping;
            }
        }
        return null;
    }

    public static TypeMapping getType(String name) {
        for (TypeMapping typeMapping : types) {
            if (typeMapping.obf.equals(name) || typeMapping.srg.equals(name)) {
                return typeMapping;
            } else {
                if (name.contains(".") || name.contains("/")) {
                    String replacedName = name.contains(".") ? name.replace(".", "/") : name.replace("/", ".");
                    if (typeMapping.obf.equals(replacedName) || typeMapping.srg.equals(replacedName)) {
                        return typeMapping;
                    }
                } else {
                    String srg = typeMapping.srg;
                    String clazz = srg.substring(srg.lastIndexOf("/") + 1, srg.length());
                    if (clazz.equals(name)) {
                        return typeMapping;
                    }
                }
            }
        }
        return null;
    }

    public static MethodMapping[] getMethods(String owner, String name, String desc) {
        TypeMapping typeMapping = getType(owner);
        if (typeMapping == null) {
            return null;
        }

        Set<MethodMapping> potentialMethods = Sets.newHashSet();

        for (MethodMapping methodMapping : methods) {
            if (methodMapping.owner.equals(typeMapping) &&
                (methodMapping.deobf.equals(name) || methodMapping.srg.equals(name) || methodMapping.obf.equals(name))) {
                potentialMethods.add(methodMapping);
            }
        }

        if (desc != null && !(desc.isEmpty())) {
            Iterator<MethodMapping> iterator = potentialMethods.iterator();
            while (iterator.hasNext()) {
                MethodMapping methodMapping = iterator.next();
                if (!(methodMapping.srgDesc.equals(desc) && !(methodMapping.obfDesc.equals(desc)))) {
                    iterator.remove();
                }
            }
        }

        return potentialMethods.toArray(new MethodMapping[potentialMethods.size()]);
    }

    public static FieldMapping getField(String owner, String name) {
        TypeMapping typeMapping = getType(owner);
        if (typeMapping == null) {
            return null;
        }

        for (FieldMapping fieldMapping : fields) {
            if (fieldMapping.owner.equals(typeMapping) &&
                    (fieldMapping.deobf.equals(name) || fieldMapping.srg.equals(name) || fieldMapping.obf.equals(name))) {
                return fieldMapping;
            }
        }

        return null;
    }

    public static void parseFiles(File methods, File fields, File joined) {
        System.out.println("Parsing mapping data. This may take a while. Don't Panic!");

        // We parse the SRG first, because it contains ownership data, as well as referencable data (name and desc)
        parseSRG(joined);

        // CSVs are parsed second, because they'll fill in the gaps left by the SRG
        parseCSV(methods, Type.METHOD);
        parseCSV(fields, Type.FIELD);
    }

    private static void parseSRG(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String[] contents = new String(ByteStreams.toByteArray(fileInputStream)).split("\n");
            for (String line : contents) {
                if (line.startsWith("PK")) {
                    continue;
                }

                if (line.startsWith("CL: ")) {
                    String[] tokens = line.substring(4, line.length()).split(" ");
                    types.add(new TypeMapping(tokens[1], tokens[0]));
                } else if (line.startsWith("FD: ")) {
                    String[] tokens = line.substring(4, line.length()).split(" ");

                    String obfMapping = tokens[0];
                    String obfType = obfMapping.substring(0, obfMapping.lastIndexOf("/"));
                    String obfField = obfMapping.substring(obfMapping.lastIndexOf("/") + 1, obfMapping.length());
                    String[] obf = new String[] {obfType, obfField};

                    String srgMapping = tokens[1];
                    String srgType = srgMapping.substring(0, srgMapping.lastIndexOf("/"));
                    String srgField = srgMapping.substring(srgMapping.lastIndexOf("/") + 1, srgMapping.length());
                    String[] srg = new String[] {srgType, srgField};

                    TypeMapping type = getType(srg[0]);

                    fields.add(new FieldMapping(type, "NULL", srg[1], obf[1], "NULL"));
                } else if (line.startsWith("MD: ")) {
                    String[] tokens = line.substring(4, line.length()).split(" ");

                    String obfMapping = tokens[0];
                    String obfType = obfMapping.substring(0, obfMapping.lastIndexOf("/"));
                    String obfField = obfMapping.substring(obfMapping.lastIndexOf("/") + 1, obfMapping.length());
                    String[] obf = new String[] {obfType, obfField};

                    String obfDesc = tokens[1];

                    String srgMapping = tokens[2];
                    String srgType = srgMapping.substring(0, srgMapping.lastIndexOf("/"));
                    String srgField = srgMapping.substring(srgMapping.lastIndexOf("/") + 1, srgMapping.length());
                    String[] srg = new String[] {srgType, srgField};

                    String srgDesc = tokens[3];

                    TypeMapping type = getType(srg[0]);

                    methods.add(new MethodMapping(type, "NULL", srg[1], obf[1], srgDesc, obfDesc, "NULL"));
                }
            }
        } catch (IOException ex) {
            System.err.println("Failed to parse " + file.getName());
        }
    }

    private static void parseCSV(File file, Type type) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String[] contents = new String(ByteStreams.toByteArray(fileInputStream)).split("\n");
            for (String line : contents) {
                if (line.equals("searge,name,side,desc"))
                    continue;

                String[] sub = line.split(",");
                String srg = sub[0];
                String deobf = sub[1];
                if (sub.length == 4) {
                    String desc = sub[3];
                    if (type == Type.METHOD) {
                        MethodMapping methodMapping = internal_getMethod(srg);
                        if (methodMapping != null) {
                            methods.remove(methodMapping);
                            MethodMapping newMapping = new MethodMapping(methodMapping.owner, deobf, srg, methodMapping.obf, methodMapping.srgDesc, methodMapping.obfDesc, desc);
                            methods.add(newMapping);
                        } else {
                            if (MCPViewer.debug) {
                                System.err.println("Failed to find method for SRG name " + srg);
                                System.err.println(srg + " => " + deobf);
                            }
                        }
                    } else if (type == Type.FIELD) {
                        FieldMapping fieldMapping = internal_getField(srg);
                        if (fieldMapping != null) {
                            fields.remove(fieldMapping);
                            FieldMapping newMapping = new FieldMapping(fieldMapping.owner, deobf, srg, fieldMapping.obf, desc);
                            fields.add(newMapping);
                        } else {
                            if (MCPViewer.debug) {
                                System.err.println("Failed to find field for SRG name " + srg);
                                System.err.println(srg + " => " + deobf);
                            }
                        }
                    }
                } else {
                    if (type == Type.METHOD) {
                        MethodMapping methodMapping = internal_getMethod(srg);
                        if (methodMapping != null) {
                            methods.remove(methodMapping);
                            MethodMapping newMapping = new MethodMapping(methodMapping.owner, deobf, srg, methodMapping.obf, methodMapping.srgDesc, methodMapping.obfDesc, "NULL");
                            methods.add(newMapping);
                        } else {
                            if (MCPViewer.debug) {
                                System.err.println("Failed to find method for SRG name " + srg);
                                System.err.println(srg + " => " + deobf);
                            }
                        }
                    } else if (type == Type.FIELD) {
                        FieldMapping fieldMapping = internal_getField(srg);
                        if (fieldMapping != null) {
                            fields.remove(fieldMapping);
                            FieldMapping newMapping = new FieldMapping(fieldMapping.owner, deobf, srg, fieldMapping.obf, "NULL");
                            fields.add(newMapping);
                        } else {
                            if (MCPViewer.debug) {
                                System.err.println("Failed to find method for SRG name " + srg);
                                System.err.println(srg + " => " + deobf);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Failed to parse " + file.getName());
        }
    }

    private enum Type {
        METHOD,
        FIELD
    }
}
