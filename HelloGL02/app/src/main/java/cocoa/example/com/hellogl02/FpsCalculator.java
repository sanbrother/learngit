package cocoa.example.com.hellogl02;

import android.util.Log;

/**
 * Created by Cocoa on 2016/11/02.
 */

public class FpsCalculator {
    private final static short MS_PER_SECOND = 1000;

    private short fps;
    private short frames;
    private long startTime;

    public FpsCalculator() {
        this.init();
    }

    public void update() {
        ++frames;

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        if (elapsedTime >= MS_PER_SECOND) {
            this.fps = (short) (frames * MS_PER_SECOND / elapsedTime);

            frames = 0;
            this.startTime = System.currentTimeMillis();

            Log.e("cocoa", "fps = " + this.fps);
        }
    }

    public short getFps() {
        return this.fps;
    }

    private void init() {
        this.frames = 0;
        this.startTime = System.currentTimeMillis();
    }
}
