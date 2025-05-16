package io.github.wj9806.minimq.broker.utils;

public class ByteUtils {

    public static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value          & 0xFF),
            (byte) ((value >> 8)  & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF),
        };
    }

    public static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF)
                | ((bytes[1] << 8) & 0xFF00)
                | ((bytes[2] << 16) & 0xFF0000)
                | ((bytes[3] << 24) & 0xFF000000);
    }
}
