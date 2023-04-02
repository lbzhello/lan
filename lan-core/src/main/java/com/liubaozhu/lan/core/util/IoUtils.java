package com.liubaozhu.lan.core.util;

import java.io.*;

public class IoUtils {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * @param in the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static final int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");

        int count = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
            count += n;
        }
        out.flush();
        return count;
    }

    /**
     * Reads all bytes from an reader and writes them to an writer
     * @param reader the stream to copy from
     * @param writer the stream to copy to
     * @return the number of chars copied
     * @throws IOException in case of I/O errors
     */
    public static final int copy(Reader reader, Writer writer) throws IOException {
        Assert.notNull(reader, "No Reader specified");
        Assert.notNull(writer, "No Writer specified");

        int count = 0;
        char[] buffer = new char[BUFFER_SIZE];
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
            count += n;
        }
        writer.flush();
        return count;
    }


}
