package com.fisher.multisdk.BaseSdk;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class BaseSdk {
    private static Activity mActivity = null;
    private static GLSurfaceView mGLSurfaceView = null;

    public static void init(Activity activity) {
        mActivity = activity;
        mGLSurfaceView = (GLSurfaceView)findGLSurfaceView(mActivity.findViewById(android.R.id.content));
    }

    private static View findGLSurfaceView(ViewGroup parent) {
        for (int i = 0;i < parent.getChildCount();i++){
            View child = parent.getChildAt(i);
            if(child instanceof GLSurfaceView){
                Log.d("BaseSDK", "Find the GLSurfaceView");
                //(GLSurfaceView)((GLSurfaceView) child).queueEvent();
                return child;
            }else if(child instanceof ViewGroup){
                return findGLSurfaceView((ViewGroup) child);
            }
        }
        Log.d("BaseSDK", "GLSurfaceView not found");
        return null;
    }

    public static void runOnGLThread(final Runnable runnable) {
        if (mActivity == null) {
            return;
        }
        assert mGLSurfaceView != null;
        mGLSurfaceView.queueEvent(runnable);
    }
}
