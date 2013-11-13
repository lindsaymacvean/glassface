/*
 * CameraActivity.java
 * Modified by Philippe Furlan on 10/21/13
 * Copyright (c) 2013 Philippe Furlan. All rights reserved.
 */

package com.glassface;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraActivity extends GoogleGlassActivity 
{
	public static final String TAG = "GlassFace";

	private CameraPreview camPreview; 
	private FrameLayout mainLayout;
	private PowerManager.WakeLock mWakeLock;
	private FaceView faceView;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.main);
		mainLayout = (FrameLayout) findViewById(R.id.frameLayout1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       
        LayoutParams layoutParamsDrawing = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        faceView = new FaceView(this,width,height);
        this.addContentView(faceView, layoutParamsDrawing);

		SurfaceView camView = new SurfaceView(this);
		SurfaceHolder camHolder = camView.getHolder();
		camPreview = new CameraPreview(this,faceView);
		
		camHolder.addCallback(camPreview);
		camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mainLayout.addView(camView);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (powerManager!=null)
		{
			mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GlassFace.wakelock");
		}
	}

	@Override
	public void onStart() 
	{
		super.onStart();
		if (mWakeLock != null)
		{
			mWakeLock.acquire();
		}
	}

	@Override
	public void onStop() 
	{
		super.onStop();
		if (mWakeLock != null)
		{
			if (mWakeLock.isHeld()) 
			{
				mWakeLock.release();
			}
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		if (camPreview!=null)
		{
			camPreview.onResume();
		}
	}

	@Override
	protected void onPause() 
	{
		if (camPreview!=null)
		{
			camPreview.onPause();
		}
		super.onPause();
	}

	//===================================================================

	public void onGoogleGlassTouchPad(int keyCode) 
	{
        switch (keyCode) 
        {
        case KeyEvent.KEYCODE_CAMERA:
            Log.d(TAG, "KEYCODE_CAMERA");
          	break;
          	
        case KeyEvent.KEYCODE_ENTER:
            Log.d(TAG, "KEYCODE_ENTER");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_CENTER:
            Log.d(TAG, "KEYCODE_DPAD_CENTER");
         	break;
          	
        case KeyEvent.KEYCODE_DPAD_DOWN:
            Log.d(TAG, "KEYCODE_DPAD_DOWN");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_LEFT:
            Log.d(TAG, "KEYCODE_DPAD_LEFT");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            Log.d(TAG, "KEYCODE_DPAD_RIGHT");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_UP:
            Log.d(TAG, "KEYCODE_DPAD_UP");
          	break;
        }   
	}

}