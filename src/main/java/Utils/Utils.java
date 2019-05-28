package Utils;

import java.nio.ByteBuffer;

public class Utils {
    public static byte[] combine(byte[]... datas) {

        int totalSize = 0;
        for (byte[] data : datas) {
            totalSize += data.length;
        }
        ByteBuffer buff = ByteBuffer.allocate(totalSize);
        for (byte[] data : datas) {
            if (data.length > 0) {
                buff.put(data);
            }
        }
        return buff.array();

    }
}
