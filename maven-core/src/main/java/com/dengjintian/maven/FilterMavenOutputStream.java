package com.dengjintian.maven;

import org.fusesource.jansi.Ansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Before content are sent to ansi, we do some extra filtering here. <br />
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
        Ansi ansi = Ansi.ansi().reset();
        if (tmp.startsWith("[INFO] -------") || (tmp.startsWith("[INFO] Building") && !tmp.contains(":"))
            || tmp.startsWith("[INFO]    task-segment") || tmp.startsWith("[INFO] Total time:")
            || tmp.startsWith("[INFO] Finished at:") || tmp.startsWith("[INFO] Final Memory:")) {
            ansi.fgBright(Ansi.Color.MAGENTA);
        } else if (tmp.startsWith("[INFO] [")) {
            ansi.fg(Ansi.Color.CYAN).bold();
        } else if (tmp.startsWith("[INFO] BUILD SUCCESSFUL")) {
            ansi.fgBright(Ansi.Color.GREEN).bold();
        } else if (tmp.startsWith("[WARNING]")) {
            ansi.fgBright(Ansi.Color.YELLOW).bold();
        } else if (tmp.startsWith("[ERROR]")) {
            ansi.fgBright(Ansi.Color.RED).bold();
        } else if (tmp.startsWith("Tests run:")) {
            String[] tokens = tmp.split(":|,");
            if (tokens.length == 8) {
                // be conservative
                ansi.fg(Ansi.Color.GREEN).a(tokens[0] + ":" + tokens[1]).reset().a(",");
                ansi.fg(Ansi.Color.GREEN).a(tokens[2] + ":").fg(Ansi.Color.RED).bold().a(tokens[3]).reset().a(",").reset();
                ansi.fg(Ansi.Color.GREEN).a(tokens[4] + ":").fg(Ansi.Color.RED).bold().a(tokens[5]).reset().a(",").reset();
                ansi.fg(Ansi.Color.GREEN).a(tokens[6] + ":").fg(Ansi.Color.YELLOW).bold().a(tokens[7]).reset();
                tmp = "";
            }
        }
        ansi.a(tmp).reset();
        return ansi.toString().getBytes();
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
