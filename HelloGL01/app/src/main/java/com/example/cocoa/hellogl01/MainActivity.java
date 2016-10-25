package com.example.cocoa.hellogl01;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GLSurfaceView glv = (GLSurfaceView)findViewById(R.id.glv_main);

        // without this line, app crashes
        glv.setEGLContextClientVersion(2);

        glv.setRenderer(new MyRenderer());
    }
}
