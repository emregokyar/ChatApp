//Agora Start Call Init
let client = null; // initializing the client for calls
let appId = null; // given app id
let channel = null;
let token = null; // Token we are crating in back end fetch it later
let uid = parseInt($("#current-user-id").val()); //current user id

// Declare variables for local tracks
let localAudioTrack = null;
let localVideoTrack = null;

//Display Buttons When User Starts a Call
let cancelCallButton = null;
let callSection = null;

$(window).on("load", initializeClient); // Initializing the user when page is upload
$("#video-call-icon").click(async function(e){
    await startVideoCall(e);
    if (window.stompClient) {
        window.stompClient.send("/app/makeCall", {}, JSON.stringify({
            channelId: parseInt(channel),
            callType: "VIDEO"
        }));
    }
});

// Starting a video call and creating token
async function startVideoCall(event) {
    event.preventDefault();
    channel = $("#current-channel-id").val(); ///CHECK HERE TO PUT ANOTHER CHANNEL ID DYNAMICALLY
    const tokenResponse = await fetch(`/generateAgoraToken/${parseInt(channel)}`);
    token = (await tokenResponse.json()).token;

    const appIdResponse = await fetch(`/getAppId`);
    appId = (await appIdResponse.json()).appId;

    await joinChannel();
}

// Joining a video call with
let callChannelId = null;
async function joinVideoCall() {
    //channel = $("#current-channel-id").val(); ///CHECK HERE TO PUT ANOTHER CHANNEL ID DYNAMICALLY
    const tokenResponse = await fetch(`/generateAgoraToken/${parseInt(callChannelId)}`);
    console.log(tokenResponse + " token response");
    token = (await tokenResponse.json()).token;
    console.log(token + " token response json");

    const appIdResponse = await fetch(`/getAppId`);
    appId = (await appIdResponse.json()).appId;


    await joinReceivingChannel();
}

async function joinReceivingChannel() {
    await client.join(appId, callChannelId.toString(), token, uid);
    await createLocalMediaTracks();
    displayLocalVideo();
    await client.publish([localAudioTrack, localVideoTrack]);
}

// Initialize the AgoraRTC client
function initializeClient() {
    client = AgoraRTC.createClient({ mode: "rtc", codec: "vp8" }); // rtc mode for video calls
    setupEventListeners();
}

// Join a channel and publish local media -- if the other user wants to channel this input should be triggered
// Creator user joins the channel as well
async function joinChannel() {
    await client.join(appId, channel, token, uid);
    await createLocalMediaTracks();
    displayLocalVideo();
    await client.publish([localAudioTrack, localVideoTrack]);
}

// Create local audio and video tracks
async function createLocalMediaTracks() {
    localAudioTrack = await AgoraRTC.createMicrophoneAudioTrack();
    localVideoTrack = await AgoraRTC.createCameraVideoTrack();
}

// Handle client events -- After user connects and initialized listens events
function setupEventListeners() {
    // Declare event handler for "user-published"
    client.on("user-published", async (user, mediaType) => {
        // Subscribe to media streams
        await client.subscribe(user, mediaType);
        if (mediaType === "video") {
            // Specify the ID of the DOM element or pass a DOM object.
            const remoteContainer = displayRemoteVideo(user);
            const element = document.getElementById("user" + user.uid.toString());
            user.videoTrack.play(element);
            shrinkLocalVideoIfRemoteExists();
        }
        if (mediaType === "audio") {
            user.audioTrack.play();
        }
    });

    // Handle the "user-unpublished" event to unsubscribe from the user's media tracks -- when user leaves the room
    client.on("user-unpublished", async (user) => {
        const remotePlayerContainer = document.getElementById("user" + user.uid.toString());
        remotePlayerContainer && remotePlayerContainer.remove();
        shrinkLocalVideoIfRemoteExists();
    });
}

function displayLocalVideo() {
    const localUserId = "user" + uid.toString();

    const $localVideoDisplayScreen = $(`
       <div class="position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-75 text-center d-flex flex-column justify-content-center col-12 align-items-center" id="call-section" style="z-index: 1050;">
           <div class="container d-flex flex-column justify-content-center col-12 align-items-center">
               <div class="mb-2 rounded-3 border col-12" id="${localUserId}" style="height: 50rem; width: 80rem;"></div>
               <div id="display-video-call" class="container d-flex flex-row col-12"></div>
           </div>

           <div class="d-flex justify-content-center mb-2 mt-2">
               <button type="button" class="btn btn-danger btn-lg me-sm-3 rounded-circle" style="aspect-ratio: 1/1;" id="cancel-call-button">
                   <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-telephone-x-fill" viewBox="0 0 16 16">
                       <path fill-rule="evenodd" d="M1.885.511a1.745 1.745 0 0 1 2.61.163L6.29 2.98c.329.423.445.974.315 1.494l-.547 2.19a.68.68 0 0 0 .178.643l2.457 2.457a.68.68 0 0 0 .644.178l2.189-.547a1.75 1.75 0 0 1 1.494.315l2.306 1.794c.829.645.905 1.87.163 2.611l-1.034 1.034c-.74.74-1.846 1.065-2.877.702a18.6 18.6 0 0 1-7.01-4.42 18.6 18.6 0 0 1-4.42-7.009c-.362-1.03-.037-2.137.703-2.877zm9.261 1.135a.5.5 0 0 1 .708 0L13 2.793l1.146-1.147a.5.5 0 0 1 .708.708L13.707 3.5l1.147 1.146a.5.5 0 0 1-.708.708L13 4.207l-1.146 1.147a.5.5 0 0 1-.708-.708L12.293 3.5l-1.147-1.146a.5.5 0 0 1 0-.708"/>
                   </svg>
               </button>
           </div>
       </div>
    `);

    $('body').append($localVideoDisplayScreen);
    localVideoTrack.play(localUserId);

    cancelCallButton = $("#cancel-call-button");
    callSection = $('#call-section');
}

// Display remote user's video -- Other user's video
function displayRemoteVideo(user) {
    const remoteUserId = "user" + user.uid.toString();
    const $remoteUserDisplay = $(`
        <div class="rounded-3 col-4 border remote-video" style="height: 14rem;" id="${remoteUserId}"></div>
    `);
    $('#display-video-call').append($remoteUserDisplay);
    return $remoteUserDisplay;
}

//Shrinking the video container when user joins
function shrinkLocalVideoIfRemoteExists() {
    const remoteUserContainer = $(".remote-video");
    const videoContainer = $("#user" + uid.toString());

    if (remoteUserContainer.length > 0) {
        videoContainer.css("height", "42rem");
    } else {
        videoContainer.css("height", "50rem");
    }
}

$(document).on('click', '#cancel-call-button', leaveChannel);
async function leaveChannel() {
    // Stop the local media tracks to release the microphone and camera resources
    if (localAudioTrack) {
        localAudioTrack.close();
        localAudioTrack = null;
    }

    if (localVideoTrack) {
        localVideoTrack.close();
        localVideoTrack = null;
    }
    await client.leave();
    $('#call-section').remove(); //Removing the part that we just created for call
}


//When Call Received
window.onCallReceived = async function(payload) {
    const callDto = JSON.parse(payload.body);
    const srcPhoto = callDto.callerPhotoPath ||  "/assets/person-circle.svg";
    const displayName = callDto.callerDisplayName;
    callChannelId = parseInt(callDto.channelId);
    console.log(callChannelId + " call chanel id after receiving color");

    $('body').append(receiverCall(srcPhoto, displayName));

    const callTimeout = setTimeout(() => {
        $('#call-receiving').remove();
    }, 10000);

    $("#accept-call-button").click(async function(){
        await clearTimeout(callTimeout);
        await joinVideoCall();
        $('#call-receiving').remove();
    });

    $("#decline-call-button").click(function(){
        clearTimeout(callTimeout);
        $('#call-receiving').remove();
    });
}

function receiverCall(src, name) {
    return `
        <div class="position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-75 text-center d-flex flex-column justify-content-center col-12 align-items-center" id="call-receiving" style=" z-index: 1050;">
            <div class="border rounded-3" style="height: 45rem; width: 25rem; object-fit: cover; background-color: aliceblue;">
                <div class="container d-flex flex-column justify-content-center col-12 align-items-center">
                    <img class="rounded-3 border col-12 mb-2" alt="profile photo" src="${src}" style="height: 35rem; width: 25rem; object-fit: cover;">
                    <div class="container justify-content-center col-12">
                        <h3>${name} IS CALLING...</h3>
                    </div>
                </div>

                <div class="d-flex justify-content-center mb-2 mt-2">
                    <button type="button" class="btn btn-success btn-lg me-sm-3 rounded-circle" style="aspect-ratio: 1/1;" id="accept-call-button">
                        <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-telephone-fill" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M1.885.511a1.745 1.745 0 0 1 2.61.163L6.29 2.98c.329.423.445.974.315 1.494l-.547 2.19a.68.68 0 0 0 .178.643l2.457 2.457a.68.68 0 0 0 .644.178l2.189-.547a1.75 1.75 0 0 1 1.494.315l2.306 1.794c.829.645.905 1.87.163 2.611l-1.034 1.034c-.74.74-1.846 1.065-2.877.702a18.6 18.6 0 0 1-7.01-4.42 18.6 18.6 0 0 1-4.42-7.009c-.362-1.03-.037-2.137.703-2.877z"/>
                        </svg>
                    </button>

                    <button type="button" class="btn btn-danger btn-lg me-sm-3 rounded-circle" style="aspect-ratio: 1/1;" id="decline-call-button">
                        <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-telephone-x-fill" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M1.885.511a1.745 1.745 0 0 1 2.61.163L6.29 2.98c.329.423.445.974.315 1.494l-.547 2.19a.68.68 0 0 0 .178.643l2.457 2.457a.68.68 0 0 0 .644.178l2.189-.547a1.75 1.75 0 0 1 1.494.315l2.306 1.794c.829.645.905 1.87.163 2.611l-1.034 1.034c-.74.74-1.846 1.065-2.877.702a18.6 18.6 0 0 1-7.01-4.42 18.6 18.6 0 0 1-4.42-7.009c-.362-1.03-.037-2.137.703-2.877zm9.261 1.135a.5.5 0 0 1 .708 0L13 2.793l1.146-1.147a.5.5 0 0 1 .708.708L13.707 3.5l1.147 1.146a.5.5 0 0 1-.708.708L13 4.207l-1.146 1.147a.5.5 0 0 1-.708-.708L12.293 3.5l-1.147-1.146a.5.5 0 0 1 0-.708"/>
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    `;
}




