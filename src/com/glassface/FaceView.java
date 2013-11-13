/*
 * FaceView.java
 * Modified by Philippe Furlan on 10/21/13
 * Copyright (c) 2013 Philippe Furlan. All rights reserved.
 */

package com.glassface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.view.View;

public class FaceView extends View
{
	private boolean haveFace;
	private Paint drawingPaint;
	private FaceDetector.Face[] detectorFaces;
	private android.hardware.Camera.Face[] detectedFaces;
	private int mWidth;
	private int mHeight;
    private PointF midEyes = new PointF();
    private PointF lt = new PointF();

	public FaceView(Context context,int width,int height) 
	{
		super(context);
		
		 mWidth = width;
		 mHeight = height;

		haveFace = false;
		drawingPaint = new Paint();
		drawingPaint.setColor(Color.GREEN);
		drawingPaint.setStyle(Paint.Style.STROKE); 
		drawingPaint.setStrokeWidth(2);
	}
	
	public void setHaveFace(boolean h)
	{
		haveFace = h;
	}

	public boolean getHaveFace()
	{
		return haveFace;
	}
	
	public void setDetectedFaceDetector(FaceDetector.Face[] faces)
	{
		detectorFaces = faces;
	}

	public void setDetectedFace(android.hardware.Camera.Face[] faces)
	{
		detectedFaces = faces;
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if(haveFace)
		{
			if (detectorFaces!=null)
			{
				for(int i=0; i<detectorFaces.length; i++)
				{					
			        detectorFaces[i].getMidPoint( midEyes );
			        float eyedist = detectorFaces[i].eyesDistance();
			        lt.x = midEyes.x - eyedist * 2.0f;
			        lt.y = midEyes.y - eyedist * 2.5f;				  
			        int left	= Math.max( (int) ( lt.x ), 0 );
			        int top		= Math.max( (int) ( lt.y ), 0 );
			        int right	= Math.min( (int) ( lt.x + eyedist * 4.0f ), mWidth);
			        int bottom	= Math.min( (int) ( lt.y + eyedist * 5.5f ), mHeight);
					canvas.drawRect(left, top, right, bottom, drawingPaint);
				}
			}
			else if (detectedFaces!=null)
			{
				for(int i=0; i<detectedFaces.length; i++)
				{					
					int l = detectedFaces[i].rect.left;
					int t = detectedFaces[i].rect.top;
					int r = detectedFaces[i].rect.right;
					int b = detectedFaces[i].rect.bottom;
					int left	= (l+1000) * mWidth/2000;
					int top		= (t+1000) * mHeight/2000;
					int right	= (r+1000) * mWidth/2000;
					int bottom	= (b+1000) * mHeight/2000;
					canvas.drawRect(left, top, right, bottom, drawingPaint);
				}
			}
		}
		else
		{
			canvas.drawColor(Color.TRANSPARENT);
		}
	}	
}