/*
 * CameraPreview.java
 * Modified by Philippe Furlan on 10/21/13
 * Copyright (c) 2013 Philippe Furlan. All rights reserved.
 */

package com.glassface;


import static com.glassface.CameraActivity.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Camera.Face;
import android.media.FaceDetector;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.FaceDetectionListener
{
	private Camera mCamera = null;
	private long timestamp;
	private FaceView mFaceView;
    private Size previewSize;
    private int[] previewPixels;
    private FaceDetector.Face faces[];

    public CameraPreview(Context context,FaceView faceView)
    {
    	mFaceView= faceView;
    }

    public void onResume() 
	{
	}

	public void onPause() 
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) 
	{
		mCamera = Camera.open();
		try
		{
			mCamera.setFaceDetectionListener(this);
			mCamera.setPreviewDisplay(surfaceHolder);
		} 
		catch (IOException e)
		{
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{
        mCamera.stopFaceDetection();
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		
	    previewPixels = null;
	}
	
	private void parametersDisplay(Parameters parameters)
	{
	    int maxNumFocusAreas = mCamera.getParameters().getMaxNumFocusAreas();
	    int maxNumMeteringAreas = mCamera.getParameters().getMaxNumMeteringAreas();
	    	    
	    String f=   parameters.getFocusMode();
	    
	    Log.d(TAG,"focus#="+maxNumFocusAreas+" metering="+maxNumMeteringAreas+" focus="+f);
	    
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) 
	    {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	        Log.d(TAG, "Camera#"+i+" "+info.orientation+" "+info.facing);
	    }

	    List<String> supportedFocusModeList = parameters.getSupportedFocusModes();
	    for (int i=0;i<supportedFocusModeList.size();i++)
	    {
	    	String s = (String) supportedFocusModeList.get(i);
	        Log.d(TAG, "focus#"+i+" = " + s); 
	    }
	    
	    List<String> supportedWhiteList = parameters.getSupportedWhiteBalance();
	    for (int i=0;i<supportedWhiteList.size();i++)
	    {
	    	String s = (String) supportedWhiteList.get(i);
	        Log.d(TAG, "white#"+i+" = " + s); 
	    }
	    
	    Log.d(TAG, "isAutoExposureLockSupported="+parameters.isAutoExposureLockSupported());
	    Log.d(TAG, "isAutoWhiteBalanceLockSupported="+parameters.isAutoWhiteBalanceLockSupported());
	    Log.d(TAG, "isSmoothZoomSupported="+parameters.isSmoothZoomSupported());
	    Log.d(TAG, "isVideoSnapshotSupported="+parameters.isVideoSnapshotSupported());
	    Log.d(TAG, "isVideoStabilizationSupported="+parameters.isVideoStabilizationSupported());
	    Log.d(TAG, "isZoomSupported="+parameters.isZoomSupported());
	    	    
	    Size pisize = parameters.getPictureSize();
	    Log.d(TAG,"PictureSize="+pisize.width+"x"+pisize.height);
//	    List<Size> sizes = parameters.getSupportedPictureSizes();
	    
	    Size previewSize = parameters.getPreviewSize();
	    Log.d(TAG,"PreviewSize="+previewSize.width+"x"+previewSize.height);
	    List<Size> previewSizes = parameters.getSupportedPreviewSizes();
	    for (int i=0;i<previewSizes.size();i++)
	    {
	    	Size result = (Size) previewSizes.get(i);
	        Log.d(TAG, "PreviewSize"+i+"=" + result.width + "x" + result.height); 
	    }    
	    
		Log.d(TAG,"Max Faces: " + parameters.getMaxNumDetectedFaces());
		Log.d(TAG,"getWhiteBalance: " + parameters.getWhiteBalance());
		Log.d(TAG,"getZoom: " + parameters.getZoom());

		
	    int[] fpsRange = {0,0};
	    parameters.getPreviewFpsRange(fpsRange);
        Log.d(TAG, "fpsRange=" + fpsRange[0] + "," + fpsRange[1]); 
	    List<int[]> fpsRanges = parameters. getSupportedPreviewFpsRange();
	    for (int i=0;i<fpsRanges.size();i++)
	    {
	    	int[] result = (int[]) fpsRanges.get(i);
	        Log.i(TAG, "fpsRange#"+i+"=" + result[0] + "," + result[1]); 
	    }
	    
        Log.d(TAG, "previewFormat: " + parameters.getPreviewFormat()); 
        List<Integer> previewFormats = parameters.getSupportedPreviewFormats();
	    for (int i=0;i<previewFormats.size();i++)
	    {
	    	int result = (int) previewFormats.get(i);
	    	if (result==17)
	    	{
	    		Log.i(TAG, "previewFormat#"+i+"=NV21");	// 460800
	    	}
	    	else if (result==20)
	    	{
	    		Log.i(TAG, "previewFormat#"+i+"=YUY2");
	    	}
	    	else if (result==842094169)
	    	{
	    		Log.i(TAG, "previewFormat#"+i+"=YV12");
	    	}	    	
	    	else
	    	{
	    		Log.i(TAG, "previewFormat#"+i+"=" + result); 
	    	}
	    }
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int w, int h) 
	{
		Log.d(TAG,"surfaceChanged:"+format+" "+w+"x"+h);
		if (h==360)
		{
			h=480;
		}
	    Parameters parameters = mCamera.getParameters();
	    int previewFormat = parameters.getPreviewFormat();
		parameters.setPreviewSize(w,h);
		mCamera.setParameters(parameters);
	    previewSize = mCamera.getParameters().getPreviewSize();
	    previewPixels = new int[w * h];
		
//		parametersDisplay(mCamera.getParameters());
		
	    int dataBufferSize=(int)(w*h* (ImageFormat.getBitsPerPixel(previewFormat)/8.0));
		mCamera.addCallbackBuffer(new byte[dataBufferSize]);
		mCamera.setPreviewCallbackWithBuffer(this);
		mCamera.startPreview();
		mCamera.startFaceDetection();
	}
	
	public void onFaceDetection(Face[] faces, Camera camera) 
	{  
		if (faces.length == 0)
		{
			if (mFaceView.getHaveFace()==true)
			{
				mFaceView.setHaveFace(false);
				mFaceView.invalidate();
			}
		}
		else
		{
			Log.d(TAG,"Faces="+faces.length);
			mFaceView.setHaveFace(true);
			mFaceView.setDetectedFace(faces);
			mFaceView.invalidate();
		}
    }

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) 
	{
//	    Log.d(TAG,"Time Gap = "+(System.currentTimeMillis()-timestamp));
	    timestamp=System.currentTimeMillis();	    
	    if (data==null)
	    {
		    decodeYUV420SP(previewPixels, data, previewSize.width, previewSize.height);
		    Bitmap bitmap = Bitmap.createBitmap(previewPixels, previewSize.width, previewSize.height, Bitmap.Config.RGB_565);

		    faces = new FaceDetector.Face[1];
		    FaceDetector detector = new FaceDetector( bitmap.getWidth(), bitmap.getHeight(), 1 );
		    int count = detector.findFaces( bitmap, faces );
		    Log.d(TAG,"FACE="+count+"    "+(System.currentTimeMillis()-timestamp));
		    if (count == 0)
		    {
		    	if (mFaceView.getHaveFace()==true)
		    	{
		    		mFaceView.setHaveFace(false);
		    		mFaceView.invalidate();
		    	}	
		    }
		    else
		    {
		    	Log.d(TAG,"FaceDetector="+faces.length);
		    	mFaceView.setHaveFace(true);
		    	mFaceView.setDetectedFaceDetector(faces);
		    	mFaceView.invalidate();
		    }
	    }
	    camera.addCallbackBuffer(data);
	}
	
	static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) 
	{
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) 
		{
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) 
			{
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
				{
					y = 0;
				}
				if ((i & 1) == 0) 
				{
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
				{
					r = 0;
				}
				else if (r > 262143)
				{
					r = 262143;
				}
				if (g < 0)
				{
					g = 0;
				}
				else if (g > 262143)
				{
					g = 262143;
				}
				if (b < 0)
				{
					b = 0;
				}
				else if (b > 262143)
				{
					b = 262143;
				}
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}	
}
