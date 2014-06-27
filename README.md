Dais
====

###Live Presentation feedback and analytics for Google Glass.

####Helps you improve visual spread and maintain consistent speech volume. 

![Live feedback](http://hwray.github.io/Dais/img/glassFeedback.png)
![Analytics dashboard](http://hwray.github.io/Dais/img/webapp.png)

####Before presenting, perform the simple calibration steps

* **Tap while looking left and then right.** This stores compass headings which are used to define the boundaries of the audience. 
* **Tap and say the first sentence of your presentation.** This is used to determine the desired speech volume for your presentation. 

![Look left](http://i102.photobucket.com/albums/m93/hwray/lookleft_zps5a4dc59a.png)
![Speech calibration](http://i102.photobucket.com/albums/m93/hwray/calibratingspeech_zps1f03f9a7.png)

####During your presentation, Dais provides (optional) live micro-feedback to help improve your performance

* A **left/right arrow** indicates that you haven't looked at the other side of the audience in a while. 

![Look right](http://i102.photobucket.com/albums/m93/hwray/rightarrow_zps76d82be8.png)

* An **up arrow** indicates that you've been looking down at your notes for too long, and should look back up at the audience. 

![Look up](http://i102.photobucket.com/albums/m93/hwray/uparrow_zps8a7510f4.png)

* A **"Face forward!"** prompt indicates that you're facing away from the audience. Useful for slide-based presentations, where it can be tempting to turn your back on your audience and read off your slides. 

![Face forward](http://i102.photobucket.com/albums/m93/hwray/faceforward_zps77cfd9e5.png)

* A **"Speak up!"** prompt indicates that the volume of your voice has fallen too far below the ideal speech threshold for your presentation. In other words: stop mumbling. 

![Speak up](http://i102.photobucket.com/albums/m93/hwray/speakup_zpsc9113039.png)

* The rest of the time, Dais maintains a blank screen and stays out of your way. 

####After your presentation, view analytics about your performance using Glass or the companion web app

#####A heatmap of your head orientation summarizes your **visual spread across the audience**, and shows you where to focus next time.

 ![Visual spread heatmap](http://i102.photobucket.com/albums/m93/hwray/heatmap1edit_zps9e1cb938.png)
 
* This presenter focused too much on the right side of the audience. 

#####A line graph of your voice level over time shows how well you maintained your **ideal speech volume**. 

![Speech volume graph](http://i102.photobucket.com/albums/m93/hwray/audio3edit_zpsc2655733.png)

* This presenter started and ended their presentation with a strong voice, but dropped down to the "mumble threshold" during the middle. 

####As an open-source app, Dais is ripe for extensions and enhancements: 

Dais uses example code from the [GDK Compass](https://github.com/googleglass/gdk-compass-sample) and [GDK Waveform](https://github.com/googleglass/gdk-waveform-sample) samples. 
