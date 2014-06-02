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
  var FIXED_HEIGHT = 150;
  var Y_AXIS_SECTIONS = 4;
  var X_AXIS_SEPARATIONS = 40;

  var soundHeights = [];

  var container = document.createElement("canvas");
  container.style.background = 'white';
  container.height = 200;

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

    // draw axes
    ctx.lineWidth = 2;
    drawHorizontalLine(ctx, '#000', 0, numDecibels);
    drawVerticalLine(ctx, '#000', FIXED_HEIGHT, 0);

    ctx.lineWidth = 1;
    // background volume
    drawHorizontalLine(ctx, '#0f0', backgroundVolume, numDecibels);
    // desired volume 
    drawHorizontalLine(ctx, '#00f', desiredVolume, numDecibels);

    // draw line graph
    ctx.strokeStyle = '#f00';
    ctx.beginPath();
    ctx.moveTo(getXCoordinate(0),0);

    for(var i=0; i< numDecibels; i++){
      var ratio = Math.abs((decibels[i] - softest) / range);
      var height = FIXED_HEIGHT - ratio*FIXED_HEIGHT;
      soundHeights[i] = height;
      ctx.lineTo(getXCoordinate(i), getYCoordinate(Math.round(height)));
    }

    // decibel labels
    for(var i=0; i< Y_AXIS_SECTIONS; i++){
      var amount = softest + (i/Y_AXIS_SECTIONS *range);
      ctx.fillText( i + ' dB', getXCoordinate(-30),  getYCoordinate(FIXED_HEIGHT - i/Y_AXIS_SECTIONS*FIXED_HEIGHT));
    }

    // time labels
    ctx.fillText('Time in Presentation (MM:SS)', numDecibels/4 ,getYCoordinate(FIXED_HEIGHT + 30));
    for(var i=0; i< numDecibels; i += X_AXIS_SEPARATIONS){
      var minutes = Math.floor(i/50);
      var seconds = Math.floor(i % 50);
      ctx.fillText( minutes + ':' +seconds, getXCoordinate(i),  getYCoordinate(FIXED_HEIGHT + 15));
    }

    ctx.stroke();


  }

  return container;

  function drawHorizontalLine(ctx, color, height, width){
    ctx.strokeStyle = color;
    ctx.beginPath();
    ctx.moveTo(getXCoordinate(0), getYCoordinate(Math.round(FIXED_HEIGHT - height * FIXED_HEIGHT)));
    ctx.lineTo(getXCoordinate(width), getYCoordinate(Math.round(FIXED_HEIGHT - height * FIXED_HEIGHT)));
    ctx.stroke();
  }
  function drawVerticalLine(ctx, color, height, width){
    ctx.strokeStyle = color;
    ctx.beginPath();
    ctx.moveTo(getXCoordinate(width), 0);
    ctx.lineTo(getXCoordinate(width), getYCoordinate(Math.round(height)));
    ctx.stroke();
  }

  function getXCoordinate(width){
    var X_OFFSET = 50;
    return width + X_OFFSET;
  }

  function getYCoordinate(height){
    var Y_OFFSET = 10;
    return height + Y_OFFSET;
  }

}

Presentation.prototype.getTotalLeft = function() {
  return this.orientations.indexOf(this.mCenterHeading);
}

Presentation.prototype.getTotalRight = function() {
  return this.orientations.length - this.orientations.indexOf(this.mCenterHeading);
}