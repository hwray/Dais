package com.stanford.dais; 

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.os.Build;

import com.firebase.client.*;

public class PrepPresentationActivity extends Activity {

	private Globals g; 
	
	private View mMainView; 
	private TextView mTitleView; 
	private TextView mHeadingView; 

	private TextView mLeftHeadingView; 
	private TextView mRightHeadingView; 

    private static final float TOO_LONG_GAZE_TIME = 100.0f; 
    private static final float TOO_STEEP_PITCH_DEGREES = 10.0f;
    
    private OrientationManager mOrientationManager;        
    private boolean mInterference; 
    
    private SensorManager mSensorManager; 
    private LocationManager mLocationManager; 
    
    private StepDetector mStepDetector; 
    
    private GestureDetector mGestureDetector;
    
    private Thread mGazeThread;  
    
    private Handler uiHandler; 

    /* FIREBASE GLOBALS */
     Firebase connection;
	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
        	if (g.pres.mLeftHeading == 0 || g.pres.mRightHeading == 0) {
            	mHeadingView.setText("" + mOrientationManager.getHeading());
        	}
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
        	// Do nothing
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
            mInterference = orientationManager.hasInterference();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_presentation);
        
        this.g = (Globals) getApplication(); 
        
        g.clearGlobals(); 
        mInterference = false; 
        
        mMainView = (View) findViewById(R.id.prep_presentation_container); 
        mHeadingView = (TextView) findViewById(R.id.compass_heading); 
        mTitleView = (TextView) findViewById(R.id.instructions_and_feedback); 
        mLeftHeadingView = (TextView) findViewById(R.id.left_heading); 
        mRightHeadingView = (TextView) findViewById(R.id.right_heading); 
        
        mGestureDetector = createGestureDetector(this); 
        
        mSensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mOrientationManager = new OrientationManager(mSensorManager, mLocationManager);
        
        mOrientationManager.addOnChangedListener(mCompassListener);
        mOrientationManager.start();
        
        mStepDetector = new StepDetector(); 
        mStepDetector.addStepListener(new StepListener() {
        	public void onStep() {
        		System.out.println("ON STEP"); 
        	}
        	
        	public void passValue() {
        		
        	}
        }); 
        
        Sensor mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*| 
                Sensor.TYPE_MAGNETIC_FIELD | 
                Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        
        initHandler(); 
        //initFirebase(); 
    }
    
    
    private GestureDetector createGestureDetector(Context context) {    	
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
        	 @Override
             public boolean onGesture(Gesture gesture) {
             	
                 if (gesture == Gesture.TAP) {
                     // mAudioManager.playSoundEffect(Sounds.TAP);
                 	if (g.pres.mLeftHeading == 0) {
                 		g.pres.mLeftHeading = mOrientationManager.getHeading(); 
                 		
                 		if (g.pres.mLeftHeading < 0) {
                 			g.pres.mLeftHeading += 360; 
                 		}
                 		
                 		mLeftHeadingView.setText("" + g.pres.mLeftHeading); 
                 		mTitleView.setText("Look at right side of room and tap"); 
                 	} else if (g.pres.mRightHeading == 0) {
                 		g.pres.mRightHeading = mOrientationManager.getHeading(); 
                 		
                 		if (g.pres.mRightHeading < 0) {
                 			g.pres.mRightHeading += 360; 
                 		}
                 		
                 		if (g.pres.mRightHeading < g.pres.mLeftHeading) {
                 			g.pres.mRightHeading += 360; 
                 		}
                 		
                 		g.pres.mCenterHeading = (g.pres.mLeftHeading + g.pres.mRightHeading) / 2; 
                 		
                 		mTitleView.setText(""); 
                 		mLeftHeadingView.setText("");
                 		mRightHeadingView.setText("");
                 		mHeadingView.setText(""); 
                 		
                 		initGazeThread(); 
                 	}
                    return true;
                 } else if (gesture == Gesture.TWO_TAP) {
                     // do something on two finger tap
                     return true;
                 } else if (gesture == Gesture.SWIPE_RIGHT) {
                     // do something on right (forward) swipe
                     return true;
                 } else if (gesture == Gesture.SWIPE_LEFT) {
                     // do something on left (backwards) swipe
                     return true;
                 } else if (gesture == Gesture.SWIPE_DOWN) {
                 	g.pres.pushOnline();
                 }
                 return false;
        	 }
        });
        return gestureDetector;
    }
    
    public void initHandler() {
    	uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					mHeadingView.setText("Magnetic interference");
				} else if (msg.what == 1) {
	        		mTitleView.setText("Look up!"); 
				} else if (msg.what == 2) {
    				mTitleView.setText("Look right!"); 
				} else if (msg.what == 3) {
    				mTitleView.setText("Look left!");
				} else if (msg.what == 4) {
					mTitleView.setText("Face forward!"); 
				} else if (msg.what == 5) {
					mTitleView.setText(""); 
				}
			}
    	}; 
    }
    
    public void initFirebase() {

    }
    
    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private void initGazeThread() {
		mGazeThread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (mInterference) {
							sendUIMessage(0); 
							//Thread.sleep(100); 
							//continue; 
						}
						
			        	if (mOrientationManager.getPitch() > TOO_STEEP_PITCH_DEGREES) {
			        		sendUIMessage(1);
			        	} else {
			        		float heading = mOrientationManager.getHeading(); 
			        		
			        		if (g.pres.mRightHeading > 360) {
			        			heading += 360; 
			        		}
			        		
			        		g.pres.headings.add(heading); 
			        		
			        		if (heading > g.pres.mLeftHeading && heading < g.pres.mCenterHeading) {
			        			if (g.pres.mGazeSide != 0) {
				        			g.pres.mGazeSide = 0; 
			        				g.pres.mGazeTime = 0; 
			        			}
			        			g.pres.mGazeTime += 1; 
			        			
			        			if (g.pres.mGazeTime > TOO_LONG_GAZE_TIME) {
			        				sendUIMessage(2);
			        			} else {
			        				sendUIMessage(5); 
			        			}
			        			
			        			g.pres.mLeftTime += 1; 
			        		} 
			        		else if (heading > g.pres.mCenterHeading && heading < g.pres.mRightHeading) {
			        			if (g.pres.mGazeSide != 1) {
				        			g.pres.mGazeSide = 1; 
				        			g.pres.mGazeTime = 0; 
			        			}
			        			g.pres.mGazeTime += 1; 
			        			
			        			if (g.pres.mGazeTime > TOO_LONG_GAZE_TIME) {
			        				sendUIMessage(3); 
			        			} else {
			        				sendUIMessage(5); 
			        			}
			        			
			        			g.pres.mRightTime += 1; 
			        		} 
			        		else {
			        			sendUIMessage(4); 
			        		}
			        	}
						
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		mGazeThread.start(); 
    }
    
    public void sendUIMessage(int msg) {
		Message message = uiHandler.obtainMessage(); 
		message.what = msg; 
		uiHandler.sendMessage(message);
	}
}