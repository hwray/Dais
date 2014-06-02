/* Function: getAllPresentationsByUsername
 * Fetches all the presentations associated with a username.
 * param username - the username identifier
 * param callback - function called with presentation array as single parameter
 */
function getPresentationsByUsername(username, callback) {
  username = username.replace(".", "");
  firebaseRef = new Firebase("https://dais.firebaseio.com/" + username);
  firebaseRef.on("value", function(snapshot) {
    presentationObjects = snapshot.val();
    presentations = [];
    for (var key in presentationObjects) {
      var presentation = new Presentation(presentationObjects[key].presentation);
      presentations.push(presentation);
    }
    callback(presentations);
  });
}

/* Presentation Global Constants */
Presentation.prototype.NUM_SEGMENTS = 25;

/* Presentation Constructor and Prototypes */
function Presentation(pres) {
  this.mRightHeading = pres.mRightHeading;
  this.mCenterHeading = pres.mCenterHeading;
  this.mLeftHeading = pres.mLeftHeading;
  this.orientations = pres.headings;
  this.decibels = pres.decibels;
  this.mFloorVolume = pres.mFloorVolume;
  this.mMumbleVolume = pres.mMumbleVolume;
  this.mSpeechVolume = pres.mSpeechVolume;
  this.numSteps = pres.numSteps;
  this.mStartTime = pres.mStartTime;
  this.mEndTime = pres.mEndTime;
}

Presentation.prototype.displayHeatMap = function() {
  // sort orientations
  if(!this.orientations)
    return;
  this.orientations.sort();

  // countOrientationsBySegment()
  segmentCounters = [];
  segmentProportions = [];
  numOrientations = this.orientations.length;
  range = this.mRightHeading - this.mLeftHeading;

  nextBreakpoint = this.mLeftHeading;
  nextBreakpointIndex = 1;
  currCounter = 0;
  totalCounter = 0;
  for (var i = 0; i < numOrientations; i++) {
    if (this.orientations[i] > nextBreakpoint) {
      segmentCounters.push(currCounter);
      currCounter = 0;
      nextBreakpointIndex++;
      nextBreakpoint = this.mLeftHeading + nextBreakpointIndex.toFixed(2)*(range / this.NUM_SEGMENTS.toFixed(2));
    } else {
      currCounter++;
    }
    totalCounter++;
  }

  for (var i = 0; i < this.NUM_SEGMENTS; i++) {
    if (i < segmentCounters.length) {
      segmentProportions.push(segmentCounters[i].toFixed(2)/totalCounter);
    } else {
      segmentProportions.push(0.0);
    }
  }

  var container = document.createElement("div");
  container.className = "heat-map";
  for (var i = 0; i < this.NUM_SEGMENTS; i++) {
    // colorHeatmapSegment
    var redColor = segmentProportions[i] * 1000;
    var heatBar = document.createElement("div");
    heatBar.className = "heat-bar";
    heatBar.style.background = "rgb(" + redColor.toFixed(0) + ", 0,0)";
    container.appendChild(heatBar);
  }
  return container;
}

Presentation.prototype.displayVolumeMap = function(){
  var container = document.createElement("canvas");
  container.style.background = 'white';

  var decibels = this.decibels;
  var loudest = Math.max.apply(Math, decibels);
  var softest = Math.min.apply(Math, decibels);
  var range = Math.abs(loudest - softest);

  var numDecibels = decibels.length;

  var backgroundVolume = Math.abs((this.mFloorVolume - softest) / range);
  var desiredVolume = Math.abs((this.mSpeechVolume - softest) / range);
  var mumbleVolume = Math.abs((this.mMumbleVolume - softest) / range);

  if(container.getContext){
    var ctx = container.getContext('2d');
    ctx.strokeStyle = '#f00';
    ctx.beginPath();
    ctx.moveTo(0,0);

    for(var i=0; i< numDecibels; i++){
      var ratio = Math.abs((decibels[i] - softest) / range);
      var height = ratio*100;
      ctx.lineTo(i, Math.round(height));
    }
    ctx.stroke();

    // background volume 
    ctx.strokeStyle = '#0f0';
    ctx.beginPath();
    ctx.moveTo(0, Math.round(backgroundVolume * 100));
    ctx.lineTo(numDecibels, Math.round(backgroundVolume * 100));
    ctx.stroke();

    // desired volume 
    ctx.strokeStyle = '#00f';
    ctx.beginPath();
    ctx.moveTo(0, Math.round(desiredVolume * 100));
    ctx.lineTo(numDecibels, Math.round(desiredVolume * 100));
    ctx.stroke();

  }


  return container;

  function getXcoordinate(){

  }

  function getYcoordinate(){

  }

}

Presentation.prototype.getTotalLeft = function() {
  return this.orientations.indexOf(this.mCenterHeading);
}

Presentation.prototype.getTotalRight = function() {
  return this.orientations.length - this.orientations.indexOf(this.mCenterHeading);
}