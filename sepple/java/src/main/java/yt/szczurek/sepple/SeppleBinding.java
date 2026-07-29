package yt.szczurek.sepple;

import org.astonbitecode.j4rs.api.Instance;
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SeppleBinding {
    private static native Instance<String> fnnoargs();

    static {
        try {
            loadNativeLib();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void loadNativeLib() throws IOException {
        InputStream inputStream = SeppleBinding.class.getClassLoader().getResourceAsStream("natives/libsepple_java.so");
        if (inputStream == null) {
            throw new FileNotFoundException("Native lib not found");
        }

        Path tempDir = Files.createTempDirectory("sepple");
        Path targetFile = tempDir.resolve("libsepple_java.so"); // Name as needed

        Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);

        inputStream.close();

        System.load(targetFile.toAbsolutePath().toString());
    }

    public static String doCallNoArgs() {

        var inst = fnnoargs();
        return Java2RustUtils.getObjectCasted(inst);
    }
}
