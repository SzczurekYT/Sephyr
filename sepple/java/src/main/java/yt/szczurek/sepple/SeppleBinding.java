package yt.szczurek.sepple;

import net.jpountz.lz4.LZ4FrameInputStream;
import org.astonbitecode.j4rs.api.Instance;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SeppleBinding {
    private static final Path NATIVE_DIR = resolveNativeDir();
    private static final String LIB_ZIP_NAME = "sepple_java_binaries.zip.l4z";
    private static final String LIB_NAME = "sepple_java";
    public static final String NATIVE_NAME = getNativeName();

    protected static native Instance<Boolean> init(Instance<String> path, Instance<List<String>> dictionary);
    protected static native void run(Instance<WordCallback> callback);
    protected static native void stop();
    protected static native Instance<Boolean> isRunning();
    protected static native void setupLogging(Instance<LogCallback> callback);

    static {
        loadLibrary();
    }

    private static Path resolveNativeDir() {
        return Paths.get(System.getProperty("user.home", System.getProperty("user.dir")), ".sepple", "natives");
    }

    private static String getNativeName() {
        boolean isArch = System.getProperty("os.arch").equals("arm") || System.getProperty("os.arch").startsWith("aarch64");
        final String arch = isArch ? "aarch64" : "x86_64";

        final String osName = System.getProperty("os.name", "").toLowerCase();
        String osSuffix;
        if (osName.contains("windows")) {
            osSuffix = "_windows.dll";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            osSuffix = "_macos.dylib";
        } else {
            osSuffix = "_linux.so";
        }
        return LIB_NAME + "_" + arch + osSuffix;
    }

    private static void loadLibrary() {
        try (final InputStream is = SeppleBinding.class.getResourceAsStream("/natives/" + LIB_NAME + "/" + LIB_ZIP_NAME)) {
            if (is == null) {
                throw new FileNotFoundException(LIB_ZIP_NAME);
            }

            if (!Files.exists(NATIVE_DIR)) {
                Files.createDirectories(NATIVE_DIR);
            }

            try (final LZ4FrameInputStream is2 = new LZ4FrameInputStream(is);
                 final ZipInputStream ti = new ZipInputStream(is2)) {

                ZipEntry entry;
                while ((entry = ti.getNextEntry()) != null) {
                    if (entry.getName().equals(NATIVE_NAME)) {
                        final Path tempFile = NATIVE_DIR.resolve(NATIVE_NAME);
                        if (Files.exists(tempFile)) {
                            Files.delete(tempFile);
                        }
                        Files.createFile(tempFile);
                        Files.copy(ti, tempFile, StandardCopyOption.REPLACE_EXISTING);
                        System.load(tempFile.toAbsolutePath().toString());
                        return;
                    }
                }

                throw new FileNotFoundException(NATIVE_NAME);
            }
        } catch (final Throwable t) {
            throw new RuntimeException("Failed to load sepple native library " + NATIVE_NAME + " from " + NATIVE_DIR, t);
        }
    }
}
