package cocoa.example.com.hellogl02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Cocoa on 2016/11/01.
 */

public class Rectangle implements Shape {
    private final Context context;

    private int mPositionsBufferIdx;

    private int mProgramHandle;
    private int mPositionHandle;

    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    /** These are handles to our texture data. */
    private int mTexture0Handle;

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

    public Rectangle(Context context) {
        this.context = context;

        initRectangleVertices();

        initProgram();

        initViewMatrix();

        initTextures();

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
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
    public void onDrawFrame() {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgramHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);

        // Tell the texture uniform sampler to use this texture in the
        // shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture information
        // GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubePositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 20, 12);

        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture0Handle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void initRectangleVertices() {
        final Vertex[] vertices = new Vertex[] {
                new Vertex(-0.8f, -0.8f, 0.0f, 0.0f, 0.0f),
                new Vertex(-0.8f,  0.8f, 0.0f, 0.0f, 1.0f),
                new Vertex(0.8f,  -0.8f, 0.0f, 1.0f, 0.0f),
                new Vertex(0.8f,   0.8f, 0.0f, 1.0f, 1.0f)
        };

        short totalBytes = 0;

        for (Vertex v : vertices) {
            totalBytes += v.toBytes().length;
        }

        ByteBuffer bf = ByteBuffer.allocateDirect(totalBytes).order(ByteOrder.nativeOrder());

        for (Vertex v : vertices) {
            bf.put(v.toBytes());
        }

        bf.position(0);

        final int buffers[] = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);

        mPositionsBufferIdx = buffers[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionsBufferIdx);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bf.capacity(), bf, GLES20.GL_STATIC_DRAW);
    }

    private void initViewMatrix() {
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
    }

    private void initTextures() {
        // Load the texture
        mTexture0Handle = TextureHelper.loadTexture(this.context, R.drawable.cocoa);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture0Handle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture0Handle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void initProgram() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(this.context, R.raw.simple_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(this.context, R.raw.simple_fragment_shader);
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        // create empty OpenGL ES Program
        mProgramHandle = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgramHandle, vertexShaderHandle);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgramHandle, fragmentShaderHandle);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgramHandle);
    }
}
