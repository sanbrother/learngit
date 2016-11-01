package cocoa.example.com.hellogl02;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Cocoa on 2016/11/01.
 */

public class Vertex {
    /** How many bytes per float. */
    static final int BYTES_PER_FLOAT = 4;

    static final int POSITION_NUM = 3;
    static final int TEXCOORDS_NUM = 2;

    private final float[] positions = new float[POSITION_NUM];
    private final float[] texCoords = new float[TEXCOORDS_NUM];

    public Vertex(float x, float y, float z, float s, float t) {
        this.positions[0] = x;
        this.positions[1] = y;
        this.positions[2] = z;

        this.texCoords[0] = s;
        this.texCoords[1] = t;
    }

    public byte[] toBytes() {
        ByteBuffer bf = ByteBuffer.allocate((POSITION_NUM + TEXCOORDS_NUM) * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());

        for (float f : this.positions) {
            bf.putFloat(f);
        }

        for (float f : this.texCoords) {
            bf.putFloat(f);
        }

        return bf.array();
    }
}
