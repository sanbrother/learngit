package cocoa.example.com.hellogl02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cocoa on 2016/11/01.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    /** How many bytes per float. */
    static final int BYTES_PER_FLOAT = 4;

    private final Context context;

    private Shape shape;

    public MyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        this.shape = new Rectangle(this.context);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.shape.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        this.shape.onDrawFrame();
    }
}
