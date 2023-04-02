package com.liubaozhu.lan.core.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileUtils {
    public static final int BUFFER_SIZE = 4096;

    public static final void copy(File from, File to) throws IOException {
        Assert.notNull(from, "No input File specified");
        Assert.notNull(to, "No output File specified");
        IoUtils.copy(Files.newInputStream(from.toPath()), Files.newOutputStream(to.toPath()));
    }

    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     * @param file the file to copy from (may be {@code null} or empty)
     * @param charset the charset of the file
     * @return the String that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    public static final String toString(File file, Charset charset) throws IOException {
        Assert.notNull(charset, "the charset can't be null!");

        if (file == null) {
            return "";
        }

        StringWriter writer = new StringWriter();
        IoUtils.copy(Files.newBufferedReader(file.toPath(), charset), writer);

        return writer.toString();
    }

    public static final String toString(File file, String charset) throws IOException {
        return toString(file, Charset.forName(charset));
    }

    public static final String toString(File file) throws IOException {
        return toString(file, Charset.defaultCharset());
    }

}
