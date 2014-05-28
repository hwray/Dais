package com.stanford.dais;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {
	
    /* Audio manager used to play system sound effects */
    private AudioManager mAudioManager;

    /* Gesture detector used to detect taps and swipes */
    private GestureDetector mGestureDetector;
    
    /*
     * Handler used to post requests to start new activities so that the menu closing animation
     * works properly.
     */
    private final Handler mHandler = new Handler(); 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = createGestureDetector(this); 
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
    
    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
            gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
                @Override
                public boolean onGesture(Gesture gesture) {
                    if (gesture == Gesture.TAP) {
                        mAudioManager.playSoundEffect(Sounds.TAP);
                        openOptionsMenu();
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
                    }
                    return false;
                }
            });
            return gestureDetector;
        }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// The startXXX() methods start a new activity, and if we call them directly here then
        // the new activity will start without giving the menu a chance to slide back down first.
        // By posting the calls to a handler instead, they will be processed on an upcoming pass
        // through the message queue, after the animation has completed, which results in a
        // smoother transition between activities.
        switch (item.getItemId()) {
            case R.id.prep_presentation:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startPrepPresentation(true);
                    }
                });
                return true;

            case R.id.prep_no_feedback:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startPrepPresentation(false);
                    }
                });
                return true;
                
            case R.id.view_data:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startViewData();
                    }
                });
                return true;

            default:
                return false;
        }
    }
    
    /**
     * Starts the prep presentation activity,
     * but does not finish this activity so that the splash screen
     * reappears when the user is done viewing data. 
     */
    private void startPrepPresentation(boolean liveFeedbackMode) {
    	Intent intent = new Intent(this, PrepPresentationActivity.class); 
    	Bundle bundle = new Bundle(); 
    	bundle.putBoolean("liveFeedbackMode", liveFeedbackMode);
    	intent.putExtras(bundle); 
        startActivity(intent);
    }

    /**
     * Starts the data browsing activity, 
     * but does not finish this activity so that the splash screen
     * reappears when the user is done viewing data. 
     */
    private void startViewData() {
        startActivity(new Intent(this, ViewDataActivity.class));
    }
}
