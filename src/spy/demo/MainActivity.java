package spy.demo;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

    protected static final String TAG = "cam_test";

    private CameraStream              mStream;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.activity_main, null, true);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        
        final Button button = (Button) findViewById(R.id.trackingBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mStream.setAlgorithm(CameraStream.Algorithm.TRACKING);
            }
        });
        final Button button2 = (Button) findViewById(R.id.bgsubBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mStream.setAlgorithm(CameraStream.Algorithm.BG_SUBSTRACTION);
            }
        });
        
        
        mStream = (CameraStream) findViewById(R.id.cameraStream);
        
        if (findViewById(R.id.cameraStream) == null) 
	    {
	    	Log.i("Main", "findViewById == null");
	    }
        else
	    	Log.i("Main", "findViewById not null");

	}
	
	public void changeResolution(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    CameraStream cm = (CameraStream) findViewById(R.id.cameraStream);
	    if (mStream == null) 
	    {
	    	Log.i("Main", "mStream == null");
	    	return;
	    }
	    if (!on) {
	    	cm.changeResolution(320, 240);
	    } else {
	    	cm.changeResolution(176, 144);
	        // Disable vibrate
	    }
	}
}
