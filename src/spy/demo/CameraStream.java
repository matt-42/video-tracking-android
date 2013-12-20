package spy.demo;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;



public class CameraStream extends SurfaceView implements  PreviewCallback, SurfaceHolder.Callback, Runnable {
    private static final String TAG = "Sample::SurfaceView";

    private Camera              mCamera;
    private int                 mFrameWidth;
    private int                 mFrameHeight;
    private byte[]              mFrame;
    private boolean             mThreadRun;
    private byte[]              mBuffer;
    private VideoProcess        mProcess;
    private EditText mEdit;
	private Bitmap mBitmap;
	private int[] mRGBA;


	public enum Algorithm
	{
		TRACKING, BG_SUBSTRACTION
	}
	private Algorithm 			mAlgorithm;
	
	public void setAlgorithm(Algorithm a)
	{
	  mAlgorithm = a;
	}
	
    public interface VideoProcess
    {
    	public void processFrame(byte[] data, int nrows, int ncols);
    }

    public CameraStream(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        Log.i(TAG, "Instantiated new " + this.getClass());
        mAlgorithm = Algorithm.TRACKING;
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }


    public void initializeCamera(int width, int height)
    {
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int mRGBAsize = width * height;
		mRGBA = new int[mRGBAsize];
		
    	mCamera = Camera.open(0);

        if (mCamera != null) {
        	try {
            Camera.Parameters params = mCamera.getParameters();
            mFrameWidth = width;
            mFrameHeight = height;

            //List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            //for (Camera.Size size : sizes) {
            // 	Log.e(TAG, size.width + "x" + size.height);
            //}
            mFrameWidth = width;
            mFrameHeight = height;
            params.setPreviewSize(getFrameWidth(), getFrameHeight());
        	//params.setPreviewFpsRange(0, 15);
            List<String> FocusModes = params.getSupportedFocusModes();
            if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            {
            	params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            params.setRotation(0);
            //if (Build.VERSION.SDK_INT >= 14)
            //params.setRecordingHint(true);
            mCamera.setParameters(params);

            /* Now allocate the buffer */
            params = mCamera.getParameters();
            int size = params.getPreviewSize().width * params.getPreviewSize().height;
            size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
            mBuffer = new byte[size];
            /* The buffer where the current frame will be coppied */
            mFrame = new byte [size];
            mCamera.addCallbackBuffer(mBuffer);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //    SurfaceTexture mSurfaceTexture = new SurfaceTexture(10);
    		//	mCamera.setPreviewTexture(mSurfaceTexture);
            //} else
            //mCamera.setPreviewDisplay(getHolder());

            /* Now we can start a preview */
            //mCamera.setDisplayOrientation(0);
            
            //mCamera.setPreviewDisplay(this.getHolder());
            SurfaceTexture surface = new SurfaceTexture(42);
            mCamera.setPreviewTexture(surface);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();
            
        	}
			catch (IOException e) {
				Log.e(TAG, "mCamera.setPreviewDisplay/setPreviewTexture fails: " + e);
			}

        }
                    
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (CameraStream.this) {
            System.arraycopy(data, 0, mFrame, 0, data.length);
            CameraStream.this.notify(); 
        }
        camera.addCallbackBuffer(mFrame);
    }

    public void startStream()
    {
    	Thread t = new Thread(this); 
        t.setPriority(Thread.MAX_PRIORITY); 
        t.start();
    }

    public void stop()
    {
        mThreadRun = false;
        if (mCamera != null) {
            synchronized (this) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        }
    }
    
    public void changeResolution(int w, int h)
    {
    	stop();
    	initializeCamera(w, h);
    	startStream();
    }
    
    public void run() {
        mThreadRun = true;
        Log.i(TAG, "Starting processing thread");
        while (mThreadRun) {
            synchronized (this) {
                try {
                    //Log.i(TAG, "wait for frame...");                	
                    this.wait();
                    //Log.i(TAG, "got a frame...");                	
                    //if (mProcess != null) mProcess.
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            processFrame(mFrame, mFrameWidth, mFrameHeight);
        }
    }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "CameraStream::surfaceCreated");

		// TODO Auto-generated method stub
		//initializeCamera(640,480);
		initializeCamera(320, 240);
		//initializeCamera(176, 144);
		startStream();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		stop();
		
	}

	public void setTextOutput(EditText t)
	{
		mEdit = t;
	}
    public void processFrame(byte[] data, int width, int height) {
        int[] rgba = mRGBA;
        //FindFeatures(getFrameWidth(), getFrameHeight(), data, rgba);
        
        if (mAlgorithm == Algorithm.TRACKING)
            tracking(width, height, data, rgba);
        else if (mAlgorithm == Algorithm.BG_SUBSTRACTION)
            backgroundsubstraction(width, height, data, rgba);
        Bitmap bmp = mBitmap; 
        
        bmp.setPixels(rgba, 0/* offset */, width /* stride */, 0, 0,width, height);
        //bmp = null;
        if (bmp != null) {
            Canvas canvas = getHolder().lockCanvas();
            //Canvas canvas = null;
            if (canvas != null) {
                canvas.drawBitmap(bmp, new Rect(0,0,width, height), new RectF(0,0, canvas.getWidth(), canvas.getHeight()), null);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    //public native void FindFeatures(int width, int height, byte yuv[], int[] rgba);
    public native void tracking(int width, int height, byte yuv[], int[] rgba);
    public native void backgroundsubstraction(int width, int height, byte yuv[], int[] rgba);


    static {
        System.loadLibrary("native_tracking");
    }
}
