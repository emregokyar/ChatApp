let chatButton = $("#chat-button");
let settingsButton = $("#settings-button");
let profileButton = $("#profile-button");
let singleUser = $(".single-user");
let infoPage = $("#info-page");
let singleUserChat = $("#user-chat");
let allUsers =  $("#all-users");
let settingsArea = $("#settings-area");
let userProfile = $("#user-profile");

//Get chats area
chatButton.click(function () {
    if (allUsers.hasClass("d-none")) {
        allUsers.removeClass("d-none");
    }

    if(!settingsArea.hasClass("d-none")){
       settingsArea.addClass("d-none");
    }

    if(!userProfile.hasClass("d-none")){
       userProfile.addClass("d-none");
    }

    if(infoPage.hasClass("d-none")){
        infoPage.removeClass("d-none");
    }

    if (!helpArea.hasClass("d-none")) {
        helpArea.addClass("d-none");
    }

    if (!singleUserChat.hasClass("d-none")) {
        singleUserChat.addClass("d-none");
    }

    if (!privacySetting.hasClass("d-none")) {
        privacySetting.addClass("d-none");
    }

    if (!$("#new-chat-area").hasClass("d-none")) {
        $("#new-chat-area").addClass("d-none");
    }
});

//Get single user
singleUser.click(function () {
    if (!infoPage.hasClass("d-none")) {
       infoPage.addClass("d-none");
    }
    if (singleUserChat.hasClass("d-none")) {
        singleUserChat.removeClass("d-none");
    }
});

//Get settings
settingsButton.click(function () {
    if (settingsArea.hasClass("d-none")) {
        settingsArea.removeClass("d-none");
    }

    if(!singleUserChat.hasClass("d-none")){
       singleUserChat.addClass("d-none");
    }

    if(!allUsers.hasClass("d-none")){
       allUsers.addClass("d-none");
    }

     if(!userProfile.hasClass("d-none")){
       userProfile.addClass("d-none");
    }

    if(infoPage.hasClass("d-none")){
        infoPage.removeClass("d-none");
    }

    if (!helpArea.hasClass("d-none")) {
        helpArea.addClass("d-none");
    }

    if (!privacySetting.hasClass("d-none")) {
        privacySetting.addClass("d-none");
    }

    if (!$("#new-chat-area").hasClass("d-none")) {
        $("#new-chat-area").addClass("d-none");
    }
});

//Get Profile Button
profileButton.click(function () {
    if (userProfile.hasClass("d-none")) {
        userProfile.removeClass("d-none");
    }

    if(!singleUserChat.hasClass("d-none")){
       singleUserChat.addClass("d-none");
    }

    if(!allUsers.hasClass("d-none")){
       allUsers.addClass("d-none");
    }

    if(!settingsArea.hasClass("d-none")){
       settingsArea.addClass("d-none");
    }

    if(infoPage.hasClass("d-none")){
        infoPage.removeClass("d-none");
    }

    if (!helpArea.hasClass("d-none")) {
        helpArea.addClass("d-none");
    }

    if (!privacySetting.hasClass("d-none")) {
        privacySetting.addClass("d-none");
    }

    if (!$("#new-chat-area").hasClass("d-none")) {
        $("#new-chat-area").addClass("d-none");
    }
});

//Change profile photo when user is selected
const fileInput = document.getElementById('fileInput'); //This is a hidden input
const profileImage = document.getElementById('profileImage');

fileInput.addEventListener('change', function () {
    const file = this.files[0];
    if (file) {
        profileImage.src = URL.createObjectURL(file);
        $("#camera-icon").addClass("d-none");
        $("#send-icon").removeClass("d-none");
    }
});

//CSRF calls to not changing the page when user is clicked the forms
document.getElementById("upload-photo-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    fetch('/updateProfilePhoto', {
      method: 'POST',
      body: formData
    });

    $("#camera-icon").removeClass("d-none");
    $("#send-icon").addClass("d-none");
});

document.getElementById("update-full-name-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    fetch('/updateFullName', {
      method: 'POST',
      body: formData
    });
});

document.getElementById("update-about-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    fetch('/updateAbout', {
      method: 'POST',
      body: formData
    });
});

//Back to settings page from privacy settings
let backSettingsButton= $("#back-settings-button");
let privacySetting= $("#privacy-setting");
let privacyButton= $("#privacy-button");

privacyButton.click(function () {
    if (!settingsArea.hasClass("d-none")) {
        settingsArea.addClass("d-none");
    }
    if (privacySetting.hasClass("d-none")) {
        privacySetting.removeClass("d-none");
    }
});

backSettingsButton.click(function () {
    if (!privacySetting.hasClass("d-none")) {
        privacySetting.addClass("d-none");
    }
    if (settingsArea.hasClass("d-none")) {
        settingsArea.removeClass("d-none");
    }
});

//Toggling between help page and settings page
let helpButton = $("#help-button");
let helpArea = $("#help-area");
let backSettings2= $("#back-settings-button-2");

helpButton.click(function () {
    if (helpArea.hasClass("d-none")) {
        helpArea.removeClass("d-none");
    }

    if (!settingsArea.hasClass("d-none")) {
        settingsArea.addClass("d-none");
    }
})

backSettings2.click(function () {
    if (!helpArea.hasClass("d-none")) {
        helpArea.addClass("d-none");
    }
    if (settingsArea.hasClass("d-none")) {
        settingsArea.removeClass("d-none");
    }
});

/// MESSAGE AND INPUT SETTINGS
 // audio recorder
let recorder, audio_stream;
const recordButton = document.getElementById("record-button");
recordButton.addEventListener("click", startRecording);

// stop recording
const stopButton = document.getElementById("stop-button");
stopButton.addEventListener("click", stopRecording);
stopButton.disabled = true;

// set preview
const preview = document.getElementById("audio-playback");

// set send button event
const sendAudio = document.getElementById("send-button");
sendAudio.addEventListener("click", sendRecording);

let writeMessageButton = $("#write-message-button");
let otherInputButton= $("#other-inputs-button");
let recordButtonJquery = $("#record-button")
let stopButtonJquery = $("#stop-button");
let audioPlayBackJquery = $("#audio-playback");

function startRecording() {
    // button settings
    recordButton.disabled = true;
    recordButtonJquery.fadeIn();

    stopButtonJquery.removeClass("d-none");
    stopButtonJquery.removeClass("inactive");
    stopButton.disabled = false;

    if (!audioPlayBackJquery.hasClass("d-none")) {
        audioPlayBackJquery.addClass("d-none");
    };

    if (!$("#send-container").hasClass("d-none")) {
        $("#send-container").addClass("d-none");
    };

    if (writeMessageButton.hasClass("d-none")) {
        writeMessageButton.removeClass("d-none");
    };

    if (!otherInputButton.hasClass("d-none")) {
        otherInputButton.addClass("d-none");
    };

    if (!$("#message-input").hasClass("d-none")) {
        $("#message-input").addClass("d-none");
    };

    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(function (stream) {
            audio_stream = stream;
            recorder = new MediaRecorder(stream);

            // when there is data, compile into object for preview src
            recorder.ondataavailable = function (e) {
                const url = URL.createObjectURL(e.data);
                preview.src = url;

                // set link href as blob url, replaced instantly if re-recorded
                sendAudio.href = url;
            };
            recorder.start();

            /*
            timeout_status = setTimeout(function () {
                console.log("5 min timeout");
                stopRecording();
            }, 300000);
            */
        }
    );
}

function stopRecording() {
    recorder.stop();
    audio_stream.getAudioTracks()[0].stop();

    // buttons reset
    recordButton.disabled = false;

    if (!stopButtonJquery.hasClass("inactive")) {
        stopButtonJquery.addClass("inactive");
    }

    if (!stopButtonJquery.hasClass("d-none")) {
        stopButtonJquery.addClass("d-none");
    }
    stopButton.disabled = true;

    audioPlayBackJquery.removeClass("d-none");
    audioPlayBackJquery.removeClass("d-none");
}

//Sending the record from here currently it downloads the audio
function sendRecording(){
    var name = new Date();
    var res = name.toISOString().slice(0,10);
    sendAudio.download = res + '.wav';
    writeNewMessage();
}
writeMessageButton.click(writeNewMessage)

function writeNewMessage() {
    sendAudio.href = "";
    recorder.stop();
    audio_stream.getAudioTracks()[0].stop();

    if (!stopButtonJquery.hasClass("inactive")) {
        stopButtonJquery.addClass("inactive");
    }
    if (!stopButtonJquery.hasClass("d-none")) {
        stopButtonJquery.addClass("d-none");
    }
    if (!audioPlayBackJquery.hasClass("d-none")) {
        audioPlayBackJquery.addClass("d-none");
    }
    if (!$("#send-container").hasClass("d-none")) {
        $("#send-container").addClass("d-none");
    }
    if (!writeMessageButton.hasClass("d-none")) {
        writeMessageButton.addClass("d-none");
    }
    if ($("#message-input").hasClass("d-none")) {
        $("#message-input").removeClass("d-none");
    };
    if (otherInputButton.hasClass("d-none")) {
        otherInputButton.removeClass("d-none");
    };
    recordButton.disabled = false;
    stopButton.disabled = true;
}

let otherInput = $("#other-inputs");
let otherUnputSubmitButton=  $("#file-submit-button");

otherInput.on( "change", function() {
    otherInput.removeClass("d-none");
    otherUnputSubmitButton.removeClass("d-none");
    $("#message-input").addClass("d-none");
    recordButtonJquery.addClass("d-none");
    $("#other-inputs-button").addClass("d-none");
    $("#back-button").removeClass("d-none");
} );

function backNormal() {
    otherInput.addClass("d-none");
    otherUnputSubmitButton.addClass("d-none");
    $("#message-input").removeClass("d-none");
    recordButtonJquery.removeClass("d-none");
    $("#other-inputs-button").removeClass("d-none");
    $("#back-button").addClass("d-none");
    otherInput.val("");
}
$("#back-button").click(backNormal);


$("#custom-play-button").click(function () {
    const audio = document.getElementById("audio-playback");

    if (audio.paused) {
        audio.play();

        $("#custom-play-button").html(`
            <svg xmlns="http://www.w3.org/2000/svg" width="27" height="27" fill="red" class="bi bi-pause" viewBox="0 0 16 16">
                <path d="M6 3.5a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5m4 0a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5"/>
            </svg>
        `);

    } else {
        audio.pause();
        $("#custom-play-button").html(`
            <svg xmlns="http://www.w3.org/2000/svg" width="27" height="27" fill="green" class="bi bi-play-fill" viewBox="0 0 16 16">
                <path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393"/>
            </svg>
        `);
    }

    audio.addEventListener("ended", function () {
        $("#custom-play-button").html(`
            <svg xmlns="http://www.w3.org/2000/svg" width="27" height="27" fill="green" class="bi bi-play-fill" viewBox="0 0 16 16">
                <path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393"/>
            </svg>
        `);
    });
});



///FUNCTIONS FOR CREATING NEW GROUP AND ADDING NEW USER
let newContactButton= $("#new-contact-button");
let newContactMainPage= $("#new-chat-main");
let addNewContactArea = $("#add-new-contact");

newContactButton.click(function () {
    if (!newContactMainPage.hasClass("d-none")) {
        newContactMainPage.addClass("d-none");
        addNewContactArea.removeClass("d-none");
    }
});

let newContactBackButton = $("#new-contact-back-button");
newContactBackButton.click(function () {
    newContactMainPage.removeClass("d-none");
    addNewContactArea.addClass("d-none");
});

const phoneInput = document.querySelector("#phone-input");
let itiVal = window.intlTelInput(phoneInput, {
    initialCountry: "tr",
    separateDialCode: true,
    strictMode: true,
    loadUtils: () => import("https://cdn.jsdelivr.net/npm/intl-tel-input@25.3.1/build/js/utils.js")
});
//userInfo.val(iti.getNumber());

let switchButtonNewContact = $("#switch-button-new-contact");
let newContactPhoneForm = $("#new-contact-phone-form");
let newContactEmailForm = $("#new-contact-email-form");

switchButtonNewContact.click(function () {
    newContactPhoneForm.toggleClass("d-none");
    newContactEmailForm.toggleClass("d-none");
});

let nextCreateGroupButton = $("#next-button-create-group");
let createGroupFirstPage = $("#create-group-first-page");
let createGroupSecondPage = $("#create-group-second-page");
let backFirstPageButton = $("#back-first-page");

nextCreateGroupButton.click(function () {
    createGroupFirstPage.toggleClass("d-none");
    createGroupSecondPage.toggleClass("d-none");
});

backFirstPageButton.click(function () {
    createGroupFirstPage.toggleClass("d-none");
    createGroupSecondPage.toggleClass("d-none");
})

const groupPhotoInput = document.getElementById('groupPhotoInput');
const groupPhotoImage = document.getElementById('group-photo');

groupPhotoInput.addEventListener('change', function () {
    const file = this.files[0];
    if (file) {
        groupPhotoImage.src = URL.createObjectURL(file);
    }
});

let backTonNewChatPage = $("#back-select-user-button");
let createNewGroupDiv= $("#create-new-group-area");
backTonNewChatPage.click(function () {
    createNewGroupDiv.toggleClass("d-none");
    $("#new-chat-main").toggleClass("d-none");
});

let createNewGroupButton= $("#create-new-group-button");
createNewGroupButton.click(function () {
    createNewGroupDiv.toggleClass("d-none");
    $("#new-chat-main").toggleClass("d-none");
});


let newChatButtonPlus = $("#new-chat-plus-button");
newChatButtonPlus.click(function () {
    $("#new-chat-area").toggleClass("d-none");
    allUsers.toggleClass("d-none");
});

let backMainButton = $("#back-main-button");
backMainButton.click(function () {
    $("#new-chat-area").toggleClass("d-none");
    allUsers.toggleClass("d-none");
});