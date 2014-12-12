package dmillerw.mcp.core;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import dmillerw.mcp.mapping.FieldMapping;
import dmillerw.mcp.mapping.MethodMapping;
import dmillerw.mcp.mapping.TypeMapping;

import java.io.*;

/**
 * @author dmillerw
 */
public class IOHelper {

    public static String getFileMD5(File file) {
        if (!file.exists()) {
            return "";
        }

        try {
            return Files.hash(file, Hashing.md5()).toString();
        } catch (IOException ex) {
            return "";
        }
    }

    public static void writeTypesToFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput(outputStream);

        dataOutput.writeInt(MappingLoader.types.size());
        for (TypeMapping typeMapping : MappingLoader.types) {
            IOHelper.writeType(typeMapping, dataOutput);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.close();
        outputStream.close();
    }

    public static void writeMethodsToFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput(outputStream);

        dataOutput.writeInt(MappingLoader.methods.size());
        for (MethodMapping methodMapping : MappingLoader.methods) {
            IOHelper.writeMethod(methodMapping, dataOutput);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.close();
        outputStream.close();
    }

    public static void writeFieldsToFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput(outputStream);

        dataOutput.writeInt(MappingLoader.fields.size());
        for (FieldMapping fieldMapping : MappingLoader.fields) {
            IOHelper.writeField(fieldMapping, dataOutput);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.close();
        outputStream.close();
    }

    public static void readTypesFromFile(File file) throws IOException {
        if (!file.exists())
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(ByteStreams.toByteArray(new FileInputStream(file)));
        int size = input.readInt();

        for (int i=0; i<size; i++) {
            MappingLoader.types.add(readType(input));
        }
    }

    public static void readMethodsFromFile(File file) throws IOException {
        if (!file.exists())
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(ByteStreams.toByteArray(new FileInputStream(file)));
        int size = input.readInt();

        for (int i=0; i<size; i++) {
            MappingLoader.methods.add(readMethod(input));
        }
    }

    public static void readFieldsFromFile(File file) throws IOException {
        if (!file.exists())
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(ByteStreams.toByteArray(new FileInputStream(file)));
        int size = input.readInt();

        for (int i=0; i<size; i++) {
            MappingLoader.fields.add(readField(input));
        }
    }

    // Individual mapping type handlers
    private static void writeType(TypeMapping typeMapping, ByteArrayDataOutput output) {
        output.writeUTF(typeMapping.srg);
        output.writeUTF(typeMapping.obf);
    }

    private static void writeMethod(MethodMapping methodMapping, ByteArrayDataOutput output) {
        writeType(methodMapping.owner, output);
        output.writeUTF(methodMapping.deobf);
        output.writeUTF(methodMapping.srg);
        output.writeUTF(methodMapping.obf);
        output.writeUTF(methodMapping.srgDesc);
        output.writeUTF(methodMapping.obfDesc);
        output.writeUTF(methodMapping.humanDescription);
    }

    private static void writeField(FieldMapping fieldMapping, ByteArrayDataOutput output) {
        writeType(fieldMapping.owner, output);
        output.writeUTF(fieldMapping.deobf);
        output.writeUTF(fieldMapping.srg);
        output.writeUTF(fieldMapping.obf);
        output.writeUTF(fieldMapping.humanDescription);
    }

    private static TypeMapping readType(ByteArrayDataInput input) {
        return new TypeMapping(input.readUTF(), input.readUTF());
    }

    private static MethodMapping readMethod(ByteArrayDataInput input) {
        return new MethodMapping(readType(input), input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF());
    }

    private static FieldMapping readField(ByteArrayDataInput input) {
        return new FieldMapping(readType(input), input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF());
    }
}
