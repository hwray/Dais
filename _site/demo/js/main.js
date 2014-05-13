firebaseRef = new Firebase("https://dais.firebaseio.com/demo/presentation");

// viewpoints = [1,2,3,4,5,6,7,8,9]; 
// presentation = {left_coor: -80, center_coor: 0, right_coor: 80, viewpoints: viewpoints}
// firebaseRef.set(presentation); 

firebaseRef.on("value", function(snapshot) {
  presentation = snapshot.val();
  console.log(presentation);

  // sort orientations
  presentation.orientations.sort();


  // NUM_SEGMENTS
  NUM_SEGMENTS = 25;

  // countOrientationsBySegment()
  segmentCounters = [];
  segmentProportions = [];
  numOrientations = presentation.orientations.length;
  range = presentation.mRightHeading - presentation.mLeftHeading;

  nextBreakpoint = presentation.mLeftHeading;
  nextBreakpointIndex = 1;
  currCounter = 0;
  totalCounter = 0;
  for (var i = 0; i < numOrientations; i++) {
    if (presentation.orientations[i] > nextBreakpoint) {
      segmentCounters.push(currCounter);
      currCounter = 0;
      nextBreakpointIndex++;
      nextBreakpoint = presentation.mLeftHeading + nextBreakpointIndex.toFixed(2)*(range / NUM_SEGMENTS.toFixed(2));
      // console.log("Next Breakpoint: " + nextBreakpoint);
    } else {
      currCounter++;
    }
    totalCounter++;
  }

  for (var i = 0; i < NUM_SEGMENTS; i++) {
    if (i < segmentCounters.length) {
      segmentProportions.push(segmentCounters[i].toFixed(2)/totalCounter);
      console.log("segment proportion: " + segmentProportions[i]);
    } else {
      segmentProportions.push(0.0);
    }
  }

  for (var i = 0; i < NUM_SEGMENTS; i++) {
    // colorHeatmapSegment
    var redColor = segmentProportions[i] * 1000;
    console.log("segment: " + i + " color: " + redColor);
    document.getElementById(i).style.background = "rgb(" + redColor.toFixed(0) + ", 0,0)";
  }



}); 


function createDivs() {
  var string = "";
  for (var i = 0; i < 25; i++) {
    string +='<div class="example_div" id="' + i + '"></div>';
  }
  return string;
}

