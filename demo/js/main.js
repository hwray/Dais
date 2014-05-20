firebaseRef = new Firebase("https://dais.firebaseio.com/demo/");

presentations = [];
firebaseRef.on("child_added", function(snapshot) {
    // new presentation added
    newPresentation = new Presentation(snapshot.val());
    console.log(newPresentation);
    presentations.push(newPresentation);

    document.body.innerHTML = "";
    document.body.appendChild(presentations[presentations.length - 1].displayHeatMap());
});

