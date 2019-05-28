package Frames;

import Utils.Utils;

import java.nio.ByteBuffer;
import java.util.Random;

public class DataFrame {
    private static final Random random = new Random(System.currentTimeMillis());

    // The parts composing the data frame:
    private boolean fin;
    private int rsv;
    private int opcode;
    private boolean mask;
    private byte[] maskKey = null;
    private byte[] extensionData = new byte[0];
    private byte[] applicationData = new byte[0];

    private byte[] rawData = null;

    // The calculated fields:
    private int payloadLength = -1;

    public DataFrame(boolean fin, int opcode, boolean mask, byte[] applicationData) {
        this(fin, 0, opcode, mask, null, null, applicationData);
    }

    public DataFrame(boolean fin, int rsv, int opcode, boolean mask, byte[] maskKey, byte[] extData, byte[] appData) {
        this.fin = fin;
        this.rsv = rsv;
        this.opcode = opcode;
        this.mask = mask;
        if (maskKey != null) {
            this.maskKey = maskKey;
        } else {
            this.maskKey = new byte[4];
            random.nextBytes(this.maskKey);
        }
        if (extData != null) {
            this.extensionData = extData;
        }
        if (appData != null) {
            this.applicationData = appData;
        }
        this.payloadLength = this.extensionData.length + this.applicationData.length;
        createRawData();
    }

    private void createRawData() {
        // Calculate the raw data length:
        int rawDataLength = 2; // The first byte must exist:
        if (mask) {
            rawDataLength += 4;
        }
        if (payloadLength > 125 && payloadLength <= 65535) {
            rawDataLength += 2;
        } else if (payloadLength > 65535) {
            rawDataLength += 8;
        }
        rawDataLength += payloadLength;

        ByteBuffer rawBuff = ByteBuffer.allocate(rawDataLength);

        // The first byte:
        byte firstByte = (byte) (opcode & 0x0f);
        firstByte = (byte) ((rsv << 4) | firstByte);
        firstByte = fin ? (byte) (firstByte | 0x80) : firstByte;
        rawBuff.put(firstByte);

        // The maks, payload length and extended payload length fields:
        if (payloadLength <= 125) {
            byte b = (byte) (payloadLength & 0xff);
            b = mask ? (byte) (b | 0x80) : b;
            rawBuff.put(b);
        } else if (payloadLength <= 65535) {
            byte b = (byte) (126 & 0xff);
            b = mask ? (byte) (b | 0x80) : b;
            rawBuff.put(b);
            rawBuff.putShort((short) payloadLength);
        } else {
            byte b = (byte) (127 & 0xff);
            b = mask ? (byte) (b | 0x80) : b;
            rawBuff.put(b);
            rawBuff.putLong(payloadLength);
        }

        // The mask key field:
        if (mask) {
            for (byte b : maskKey) {
                rawBuff.put(b);
            }
        }

        // The payload data field:
        byte[] payloadData = Utils.combine(extensionData, applicationData);
        if (mask) {
            for (int i = 0; i < payloadLength; i++) {
                rawBuff.put((byte) (payloadData[i] ^ maskKey[i % 4]));
            }
        } else {
            rawBuff.put(payloadData);
        }

        rawData = rawBuff.array();
    }
}
