package com.dengjintian.maven;

import org.fusesource.jansi.Ansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Before content are sent to ansi, we do some extra filters here. <br />
 * User: jintian, Date: 31/12/12
 */
public class FilterMavenOutputStream extends FilterOutputStream {

    private static int    buffer_size = 1024;
    private static byte[] buffer      = new byte[buffer_size];
    private static int    index       = 0;

    public FilterMavenOutputStream(OutputStream outputStream){
        super(outputStream);
    }

    public void write(int i) throws IOException {

        if (i == '\n' || i == '\r') {
            // wait until we get the whole line
            buffer[index++] = (byte) i;
            String line = new String(buffer);
            out.write(getColourfulByte(line));
            resetBuffer();
        } else {
            buffer[index++] = (byte) i;
            if (index >= buffer_size) {
                enlargeBuffer();
            }
        }
    }

    private byte[] getColourfulByte(String tmp) {
        Ansi ansi = Ansi.ansi();
        if (tmp.startsWith("[INFO] ----")) {
            return ansi.fg(Ansi.Color.BLUE).bold().a(tmp).toString().getBytes();
        } else if (tmp.startsWith("[INFO] [")) {
            byte[] result = ansi.reset().fg(Ansi.Color.CYAN).bold().a(tmp).reset().toString().getBytes();
            return result;
        } else if (tmp.startsWith("[INFO] BUILD SUCCESSFUL")) {
            byte[] result = ansi.reset().fg(Ansi.Color.GREEN).bold().a(tmp).reset().toString().getBytes();
            return result;
        } else if (tmp.startsWith("[WARNING]")) {
            byte[] result = ansi.reset().fg(Ansi.Color.YELLOW).bold().a(tmp).reset().toString().getBytes();
            return result;
        } else if (tmp.startsWith("[ERROR]")) {
            byte[] result = ansi.reset().fg(Ansi.Color.RED).bold().a(tmp).reset().toString().getBytes();
            return result;
        } else {
            return tmp.getBytes();
        }
    }

    private void resetBuffer() {
        buffer_size = 1024;
        index = 0;
        buffer = new byte[buffer_size];
    }

    public void enlargeBuffer() {
        buffer_size = 2 * buffer_size;
        byte[] tmp = new byte[buffer_size];
        System.arraycopy(buffer, 0, tmp, 0, buffer.length);
        buffer = tmp;
    }
}
