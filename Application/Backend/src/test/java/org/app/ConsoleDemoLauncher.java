package org.app;

import java.io.InputStream;
import java.io.IOException;

public class ConsoleDemoLauncher {
    public static void main(String[] args) throws Exception {
        InputStream original = System.in;
        System.setIn(new InputStream() {
            @Override
            public int read() throws IOException {
                int value = original.read();
                if (value != -1) {
                    System.out.write(value);
                    System.out.flush();
                }
                return value;
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (length == 0) return 0;
                int value = read();
                if (value == -1) return -1;
                buffer[offset] = (byte) value;
                return 1;
            }

            @Override
            public int available() { return 0; }
        });
        Main.main(args);
    }
}
