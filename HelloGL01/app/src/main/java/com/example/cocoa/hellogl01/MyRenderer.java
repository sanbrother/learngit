package com.example.cocoa.hellogl01;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Cocoa on 2016/10/25.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    public static final String TAG = "MyRenderer";

    /** These are handles to our texture data. */
    private int mAndroidDataHandle;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    public MyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        initVBO();

        // Load the texture
        mAndroidDataHandle = TextureHelper.loadTexture(this.context, R.drawable.cocoa);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        drawVBO();
    }

    private int mCubePositionsBufferIdx;
    private int mCubeColorsBufferIdx;
    private int mCubeTexCoordsBufferIdx;

    private void initVBO()
    {
        // Java array.
        float[] cubePositions = new float[] { -0.8f, 0.8f, -0.8f, -0.8f, 0.8f, -0.8f };

        // Floating-point buffer
        final FloatBuffer cubePositionsBuffer;

        // Allocate a direct block of memory on the native heap,
        // size in bytes is equal to cubePositions.length * BYTES_PER_FLOAT.
        // BYTES_PER_FLOAT is equal to 4, since a float is 32-bits, or 4 bytes.
        cubePositionsBuffer = ByteBuffer.allocateDirect(cubePositions.length * BYTES_PER_FLOAT)

        // Floats can be in big-endian or little-endian order.
        // We want the same as the native platform.
        .order(ByteOrder.nativeOrder())

        // Give us a floating-point view on this byte buffer.
        .asFloatBuffer();

        // Copy data from the Java heap to the native heap.
        cubePositionsBuffer.put(cubePositions)

        // Reset the buffer position to the beginning of the buffer.
        .position(0);

        final int buffers[] = new int[3];
        GLES20.glGenBuffers(buffers.length, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubePositionsBuffer.capacity() * BYTES_PER_FLOAT, cubePositionsBuffer, GLES20.GL_STATIC_DRAW);

        mCubePositionsBufferIdx = buffers[0];



        // Java array.
        float[] cubeColors = new float[] {
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        // Floating-point buffer
        final FloatBuffer cubeColorsBuffer;

        // Allocate a direct block of memory on the native heap,
        // size in bytes is equal to cubePositions.length * BYTES_PER_FLOAT.
        // BYTES_PER_FLOAT is equal to 4, since a float is 32-bits, or 4 bytes.
        cubeColorsBuffer = ByteBuffer.allocateDirect(cubeColors.length * BYTES_PER_FLOAT)

        // Floats can be in big-endian or little-endian order.
        // We want the same as the native platform.
        .order(ByteOrder.nativeOrder())

        // Give us a floating-point view on this byte buffer.
        .asFloatBuffer();

        // Copy data from the Java heap to the native heap.
        cubeColorsBuffer.put(cubeColors)

        // Reset the buffer position to the beginning of the buffer.
        .position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeColorsBuffer.capacity() * BYTES_PER_FLOAT, cubeColorsBuffer, GLES20.GL_STATIC_DRAW);

        mCubeColorsBufferIdx = buffers[1];







        // Java array.
        float[] cubeTexCoords = new float[] {
                // front
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };

        // Floating-point buffer
        final FloatBuffer cubeTexCoordsBuffer;

        // Allocate a direct block of memory on the native heap,
        // size in bytes is equal to cubePositions.length * BYTES_PER_FLOAT.
        // BYTES_PER_FLOAT is equal to 4, since a float is 32-bits, or 4 bytes.
        cubeTexCoordsBuffer = ByteBuffer.allocateDirect(cubeTexCoords.length * BYTES_PER_FLOAT)

                // Floats can be in big-endian or little-endian order.
                // We want the same as the native platform.
                .order(ByteOrder.nativeOrder())

                // Give us a floating-point view on this byte buffer.
                .asFloatBuffer();

        // Copy data from the Java heap to the native heap.
        cubeTexCoordsBuffer.put(cubeTexCoords)

                // Reset the buffer position to the beginning of the buffer.
                .position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cubeTexCoordsBuffer.capacity() * BYTES_PER_FLOAT, cubeTexCoordsBuffer, GLES20.GL_STATIC_DRAW);

        mCubeTexCoordsBufferIdx = buffers[2];





        final String vertexShader = RawResourceReader.readTextFileFromRawResource(this.context, R.raw.simple_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(this.context, R.raw.simple_fragment_shader);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShaderHandle);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShaderHandle);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
    }

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private void drawVBO()
    {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);


        int mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        int mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        // Pass in the texture information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeTexCoordsBufferIdx);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);

        // Tell the texture uniform sampler to use this texture in the
        // shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        Matrix.setIdentityM(mModelMatrix, 0);
        // Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -1.0f);

        // This multiplies the view matrix by the model matrix, and stores
        // the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        // Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        // Matrix.setIdentityM(mMVPMatrix, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubePositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        // get handle to fragment shader's vColor member
        // int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        // GLES20.glUniform4fv(mColorHandle, 1, color, 0);


        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeColorsBufferIdx);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, 0);


        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, /* vertexCount */ 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        // Disable color array
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }
}
