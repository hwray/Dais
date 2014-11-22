Dais
====

##Live presentation feedback and analytics for Google Glass.

###Helps improve your visual spread, and maintain consistent speech volume and pace. 

![Live feedback](http://hwray.github.io/Dais/img/glassFeedback.png)
![Analytics dashboard](http://hwray.github.io/Dais/img/webapp.png)

====

###Before presenting, perform the simple calibration steps.

* **Tap while looking left and then right.** This stores compass headings which are used to define the boundaries of the audience. 

![Look left](http://i102.photobucket.com/albums/m93/hwray/lookleftsmall_zps38ace988.png)

* **Tap and say the first sentence of your presentation.** This is used to determine the desired speech volume for your presentation. 

![Speech calibration](http://i102.photobucket.com/albums/m93/hwray/calibratingspeechsmall_zps6bc25010.png)

====

###During your presentation, Dais provides (optional) live micro-feedback to help improve your performance.

* A **left/right arrow** indicates that you haven't looked at the other side of the audience in a while. 

![Look right](http://i102.photobucket.com/albums/m93/hwray/rightarrowsmall_zps87d0d01e.png)

* An **up arrow** indicates that you've been looking down at your notes for too long, and should look back up at the audience. 

![Look up](http://i102.photobucket.com/albums/m93/hwray/uparrowsmall_zpsc839ae47.png)

* A **"Face forward!"** prompt indicates that you're facing away from the audience. Useful for slide-based presentations, where it can be tempting to turn your back on your audience and read off your slides. 

![Face forward](http://i102.photobucket.com/albums/m93/hwray/faceforwardsmall_zpsd8721a75.png)

* A **"Speak up!"** prompt indicates that the volume of your voice has fallen too far below the ideal speech threshold for your presentation. In other words: stop mumbling. 

![Speak up](http://i102.photobucket.com/albums/m93/hwray/speakupsmall_zpsd3a91616.png)

* The rest of the time, Dais maintains a blank screen and stays out of your way. 

====

###After your presentation, view analytics about your performance using Glass or the companion web app.

####A heatmap of your head orientation summarizes your **visual spread across the audience**, and shows you where to focus next time.

 ![Visual spread heatmap](http://i102.photobucket.com/albums/m93/hwray/heatmap1editsmall_zps7f8266cb.png)
 
* This presenter focused too much on the right side of the audience. 

####A line graph of your voice level shows how well you maintained your **ideal speech volume**. 

![Speech volume graph](http://i102.photobucket.com/albums/m93/hwray/audio3edit_zpsc2655733.png)

* This presenter started and ended their presentation with a strong voice, but dropped down to the "mumble threshold" during the middle. 

====

###As an open-source app, Dais is ripe for extensions and enhancements: 

* Integrate a **pedometer to track presenter's footsteps.** Dais could tell presenters to move around if they've been standing still too long, or tell them to plant their feet for a moment if they're pacing around too much. 
* Implement **background speech recognition** to perform a variety of tasks: 
 * Provide users a **transcript** of their presentation. 
 * Give live feedback to **discourage presenters from using "filler words"** ("uh," "um," etc.). 
 * Give live feedback to presenters to **speak slower or faster,** by analyzing speech pace (words/syllables per minute). 
* Add **gesture recognition** functionality using a **wrist-based wearable device** (like an [Android Wear](http://www.android.com/wear/) watch). Give users feedback on the frequency and variety of their hand/arm gestures, as well as other body language. 

![Moto360](http://cdn4.mos.techradar.futurecdn.net//art/other/Onetimers/android-wear-moto-360-close-up-578-80.jpg)

====

* Dais uses example code from the [GDK Compass](https://github.com/googleglass/gdk-compass-sample) and [GDK Waveform](https://github.com/googleglass/gdk-waveform-sample) samples. 
