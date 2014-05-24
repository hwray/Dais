function Presentation(pres) {
  this.mRightHeading = pres.mRightHeading;
  this.mCenterHeading = pres.mCenterHeading;
  this.mLeftHeading = pres.mLeftHeading;
  this.orientations = pres.orientations;
  if (typeof(pres.presentation) != 'undefined') {
    this.orientations = pres.presentation.headings;
    this.mRightHeading = pres.presentation.mRightHeading;
    this.mCenterHeading = pres.presentation.mCenterHeading;
    this.mLeftHeading = pres.presentation.mLeftHeading;
  }
}


/* Presentation Global Constants */
Presentation.prototype.NUM_SEGMENTS = 25;

Presentation.prototype.displayHeatMap = function() {
  // sort orientations
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

Presentation.prototype.getTotalLeft = function() {
  return this.orientations.indexOf(this.mCenterHeading);
}

Presentation.prototype.getTotalRight = function() {
  return this.orientations.length - this.orientations.indexOf(this.mCenterHeading);
}