/*
 * GoogleGlassActivity.java
 * Modified by Philippe Furlan on 9/2/13
 * Copyright (c) 2013 Philippe Furlan. All rights reserved.
 */

package com.glassface;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GoogleGlassActivity  extends Activity 
{
	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this, new GoogleGlassDPad());
	}
	
	@Override
    public boolean onGenericMotionEvent(MotionEvent event) 
	{
        mGestureDetector.onTouchEvent(event);
        return true;
    }
	
    @Override
    public void onBackPressed() 
    {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
    	switch (keyCode) 
    	{
    	case KeyEvent.KEYCODE_BACK:
    		onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_DOWN);
    		return true;

    	case KeyEvent.KEYCODE_CAMERA:
    		onGoogleGlassTouchPad(KeyEvent.KEYCODE_CAMERA);
    		return true;

    	default:
     		return super.onKeyDown(keyCode, event);
    	}
    }

    public void onGoogleGlassTouchPad(int keyCode)
    {
    	/*
        switch (keyCode) 
        {
        case KeyEvent.KEYCODE_CAMERA:
            Log.d("Event", "KEYCODE_CAMERA");
          	break;
          	
        case KeyEvent.KEYCODE_ENTER:
            Log.d("Event", "KEYCODE_ENTER");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_CENTER:
            Log.d("Event", "KEYCODE_DPAD_CENTER");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_DOWN:
            Log.d("Event", "KEYCODE_DPAD_DOWN");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_LEFT:
            Log.d("Event", "KEYCODE_DPAD_LEFT");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            Log.d("Event", "KEYCODE_DPAD_RIGHT");
          	break;
          	
        case KeyEvent.KEYCODE_DPAD_UP:
            Log.d("Event", "KEYCODE_DPAD_UP");
          	break;
        } 
        */  
    }
    
	private class GoogleGlassDPad extends GestureDetector.SimpleOnGestureListener 
	{ 
		private static final int SWIPE_MIN_DISTANCE = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 1000;

		@Override
		public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY) 
		{
			try 
			{
				float totalXTraveled = finish.getX() - start.getX();
				float totalYTraveled = finish.getY() - start.getY();
				if (Math.abs(totalXTraveled) > Math.abs(totalYTraveled)) 
				{
					if (Math.abs(totalXTraveled) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
					{
						if (totalXTraveled > 10) 
						{
							onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_RIGHT);
						} 
						else 
						{
							onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_LEFT);
						}
					}
				} 
				else 
				{
					if (Math.abs(totalYTraveled) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) 
					{
						if(totalYTraveled > 0) 
						{
							onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_DOWN);
						} 
						else 
						{
							onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_UP);
						}
					}
				}
			} 
			catch (Exception e) 
			{
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) 
		{
			onGoogleGlassTouchPad(KeyEvent.KEYCODE_ENTER);
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) 
		{
			onGoogleGlassTouchPad(KeyEvent.KEYCODE_DPAD_CENTER);
			return true;
		}
	}
}
