package maven;

import org.fusesource.jansi.Ansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Before content are sent to ansi, we do some extra filters here. <br />
 * User: jintian, Date: 31/12/12
 */
public class FilterMavenOutputStream extends FilterOutputStream {

    int    buffer_size = 1024;
    byte[] buffer      = new byte[buffer_size];
    int    index       = 0;

    public FilterMavenOutputStream(OutputStream outputStream){
        super(outputStream);
    }

    public void write(int i) throws IOException {

        if (i == '\n' || i == '\r') {
            buffer[index++] = (byte) i;
            String tmp = new String(buffer);
            if (tmp.contains("INFO")) {
                out.write(Ansi.ansi().fg(Ansi.Color.BLUE).a(tmp).toString().getBytes());
                out.write(Ansi.ansi().reset().toString().getBytes());
            } else {
                out.write((tmp).getBytes());
            }

            resetBuffer();
        } else {
            buffer[index++] = (byte) i;
            if (index >= buffer_size) {
                enlargeBuffer();
            }
        }
    }

    private void resetBuffer() {
        buffer_size = 1024;
        index=0;
        buffer = new byte[buffer_size];
    }

    public void enlargeBuffer() {
        buffer_size = 2 * buffer_size;
        byte[] tmp = new byte[buffer_size];
        System.arraycopy(buffer, 0, tmp, 0, buffer.length);
        buffer = tmp;
    }
}
