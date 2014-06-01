  $(document).ready(function() {
    $('#disconnect').click(logout);
  });

  function showPresentations(){
    getPresentationsByUsername(username, function(presentations) { 

      var heatMapContainer = document.createElement("div");
      heatMapContainer.className = 'row-fluid';
      heatMapContainer.className = 'heatmap-container';
      document.body.appendChild(heatMapContainer);

      if(presentations.length == 0){
        heatMapContainer.innerHTML ='<p>Sorry, no presentation data found!</p>';
      }

      for(var i=0; i<presentations.length; i++){
        heatMapContainer.appendChild(presentations[i].displayHeatMap());
        var hm = heatMapContainer.lastChild;
        hm.className = hm.className + ' col-sm-4';
      }
    });
  }

  /**
   * Global variables to hold the profile and email data.
   */
   var profile, email, username;

  /*
   * Triggered when the user accepts the sign in, cancels, or closes the
   * authorization dialog.
   */
  function loginFinishedCallback(authResult) {
    if (authResult) {
      if (authResult['error'] == undefined){
        toggleElement('signin-button'); // Hide the sign-in button after successfully signing in the user.
        gapi.client.load('plus','v1', loadProfile);  // Trigger request to get the email address.
      } else {
        console.log('An error occurred');
      }
    } else {
      console.log('Empty authResult');  // Something went wrong
    }
  }

  /**
   * Uses the JavaScript API to request the user's profile, which includes
   * their basic information. When the plus.profile.emails.read scope is
   * requested, the response will also include the user's primary email address
   * and any other email addresses that the user made public.
   */
  function loadProfile(){
    var request = gapi.client.plus.people.get( {'userId' : 'me'} );
    request.execute(loadProfileCallback);
  }

  /**
   * Callback for the asynchronous request to the people.get method. The profile
   * and email are set to global variables. Triggers the user's basic profile
   * to display when called.
   */
  function loadProfileCallback(obj) {
    profile = obj;

    // Filter the emails object to find the user's primary account, which might
    // not always be the first in the array. The filter() method supports IE9+.
    email = obj['emails'].filter(function(v) {
        return v.type === 'account'; // Filter out the primary email
    })[0].value; // get the email from the filtered results, should always be defined.

    var email_array = email.split('@');
    username = email_array[0];

    displayProfile(profile);
    showPresentations();
  }

  /**
   * Display the user's basic profile information from the profile object.
   */
  function displayProfile(profile){
    document.getElementById('name').innerHTML = profile['displayName'];
    document.getElementById('pic').innerHTML = '<img src="' + profile['image']['url'] + '" />';
    document.getElementById('email').innerHTML = email;
    toggleElement('profile');
  }

  /**
   * Utility function to show or hide elements by their IDs.
   */
  function toggleElement(id) {
    var el = document.getElementById(id);
    if (el.getAttribute('class') == 'hide') {
      el.setAttribute('class', 'show');
    } else {
      el.setAttribute('class', 'hide');
    }
  }

  /* Function to log out */

  function logout(){
    $.ajax({
        type: 'GET',
        url: 'https://accounts.google.com/o/oauth2/revoke?token=' +
            gapi.auth.getToken().access_token,
        async: false,
        contentType: 'application/json',
        dataType: 'jsonp',
        success: function(result) {
          console.log('revoke response: ' + result);
          toggleElement('profile');
          toggleElement('signin-button');
        },
        error: function(e) {
          console.log(e);
        }
    });
  }