firebaseRef = new Firebase("https://dais.firebaseio.com/demo/");

presentations = [];
firebaseRef.on("child_added", function(snapshot) {
    // new presentation added
    newPresentation = new Presentation(snapshot.val());
    presentations.push(newPresentation);
    document.body.appendChild(presentations[presentations.length - 1].displayHeatMap());
  });