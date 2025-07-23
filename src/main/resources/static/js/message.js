$(window).on("load", findAndDisplayConnectedUsers);

const chatSection = $(".chat-area");
const currentUserId = parseInt($("#current-user-id").val());
let channelIdentificationNumber;
let channelTypeDisplay;
let receiverIds = [];
let stompClient = null;

// Fetch and display all messaged users
async function findAndDisplayConnectedUsers() {
    const response = await fetch('/getChannels');
    const channels = await response.json();
    const channelsList = $("#channels");
    channelsList.empty();

    channels.forEach(({ photoDir, channelName, channelId, lastMessage , date, channelType }) => {
        const photoPath = photoDir || '/assets/person-circle.svg';
        const givenId = `${channelType}-${channelId}`;
        const lastMessageDisplay = lastMessage || '';

        const $channel = $(`
            <a class="list-group-item list-group-item-action py-3 lh-sm single-user" style="width: 30rem;" id="${givenId}">
                <div class="d-flex">
                    <img src="${photoPath}" alt="" class="rounded-circle" style="height: 3rem; width: 3rem; margin-right: 1rem; object-fit: cover; aspect-ratio: 1 / 1;">
                    <div style="width: 100%;">
                        <div class="d-flex w-100 justify-content-between">
                            <strong class="mb-1 text-truncate" style="max-width: 15rem;">${channelName}</strong>
                            <small>${date}</small>
                        </div>
                        <div class="small text-truncate" style="max-width: 22rem;">${lastMessageDisplay}</div>
                    </div>
                </div>
            </a>
        `);

        channelsList.append($channel);
    });
}

// When user clicks existing channels
$("#channels").on("click", ".single-user", async function () {
    toggleChatVisibility();
    channelIdentificationNumber = $(this).attr('id').split('-')[1];
    $("#current-channel-id").val(channelIdentificationNumber);

    channelTypeDisplay = $(this).attr('id').split('-')[0];
    receiverIds = await getArrayOfChannelAsync(channelIdentificationNumber);
    fetchAndDisplayUserChat();
});

function toggleChatVisibility() {
    $("#info-page").addClass("d-none");
    $("#user-chat").removeClass("d-none");
    chatSection.empty();
}

//Displaying the photos of users or groups top of the page
async function fetchAndDisplayUserOrGroupPhotos(targetId, type) {
    const response = await fetch(`/getChannelInfo/${parseInt(targetId)}`);
    const { channelName, photoDir } = await response.json();
    const fallback = type === "GROUP" ? "/assets/people-fill.svg" : "/assets/person-circle.svg";

    $("#chat-display-photo").attr("src", photoDir || fallback);
    $("#chat-display-name").text(channelName);
}

//Call function to display user chat
async function fetchAndDisplayUserChat() {
    await fetchAndDisplayUserOrGroupPhotos(channelIdentificationNumber, channelTypeDisplay);
    chatSection.empty();

    const response = await fetch(`/getMessages/${parseInt(channelIdentificationNumber)}`);
    const messages = await response.json();

    messages.forEach(displayMessage);
    scrollToBottom();
}

//Display message call
function displayMessage(message) {
    const { messageType, userDto, content, date, resourceDir } = message;
    const isSender = userDto.userId === currentUserId;
    const profilePhoto = userDto.profilePhotoPath || "/assets/person-circle.svg";
    const isGroup = channelTypeDisplay === "GROUP";

    let html = '';
    if (messageType === 'TEXT') {
        html = isSender ? senderMessage(content, date) : receiverMessage(content, date, isGroup ? profilePhoto : null);
    } else if (messageType === 'PHOTO') {
        html = isSender ? senderPhoto(resourceDir, "", date) : receiverAudio(resourceDir, "", date, isGroup ? profilePhoto : null);
    }  else if (messageType === 'AUDIO') {
        html = isSender ? senderAudio(resourceDir, "", date) : receiverAudio(resourceDir, "", date, isGroup ? profilePhoto : null);
    }
    chatSection.append($(html));
}

//Message Renders
function senderMessage(content, date) {
    return `
        <div class="chat-message sender mx-4 mt-2">
            <div class="message">
                <p style="margin-bottom: -10px;">${content}</p>
                <small style="color: grey; font-size: 12px;">${date}</small>
            </div>
        </div>
    `;
}

function receiverMessage(content, date, profilePhoto) {
    return `
        <div class="chat-message receiver">
            ${profilePhoto ? `<img src="${profilePhoto}" class="rounded-circle mt-1" height="40" width="40" style="object-fit: cover;"/>` : ''}
            <div class="message mx-4 mt-2">
                <p style="margin-bottom: -5px;">${content}</p>
                <div class="d-flex justify-content-end">
                    <small style="color: grey; font-size: 12px;">${date}</small>
                </div>
            </div>
        </div>
    `;
}

function senderPhoto(src, content, date) {
    return `
        <div class="chat-message sender mx-4 mt-2">
            <div class="message">
                <img class="rounded-3" src="${src}" alt="" style="width: 20rem; height: 20rem; object-fit: cover;">
                <p style="margin-bottom: -10px;">${content}</p>
                <small style="color: grey; font-size: 12px;">${date}</small>
            </div>
        </div>
    `;
}

function receiverPhoto(src, content, date, profilePhoto) {
    return `
        <div class="chat-message receiver">
            ${profilePhoto ? `<img src="${profilePhoto}" class="rounded-circle mt-1" height="40" width="40" style="object-fit: cover;"/>` : ''}
            <div class="message mx-1 mt-2">
                <img class="rounded-3" src="${src}" alt="" style="width: 20rem; height: 20rem; object-fit: cover;">
                <p style="margin-bottom: -5px;">${content}</p>
                <div class="d-flex justify-content-end">
                    <small style="color: grey; font-size: 12px;">${date}</small>
                </div>
            </div>
        </div>
    `;
}

function senderAudio(src, content, date) {
    return `
        <div class="chat-message sender mx-4 mt-2">
            <div class="message">
                <audio controls style="background-color: transparent !important;">
                    <source  src="${src}" type="audio/mpeg">
                    Your browser does not support the audio element.
                </audio>
                <p style="margin-bottom: -10px;">${content}</p>
                <small style="color: grey; font-size: 12px;">${date}</small>
            </div>
        </div>
    `;
}

function receiverAudio(src, content, date, profilePhoto) {
    return `
        <div class="chat-message receiver">
            ${profilePhoto ? `<img src="${profilePhoto}" class="rounded-circle mt-1" height="40" width="40" style="object-fit: cover;"/>` : ''}
            <div class="message mx-1 mt-2">
                <audio controls style="background-color: transparent !important;">
                    <source  src="${src}" type="audio/mpeg">
                    Your browser does not support the audio element.
                </audio>
                <p style="margin-bottom: -5px;">${content}</p>
                <div class="d-flex justify-content-end">
                    <small style="color: grey; font-size: 12px;">${date}</small>
                </div>
            </div>
        </div>
    `;
}

function scrollToBottom() {
    chatSection.scrollTop(chatSection[0].scrollHeight);
}

// When user clicks on the contacts
$(".contact-list").click(handleContactClick);

async function handleContactClick() {
    toggleChatVisibility();
    const contactId = $(this).attr('id').split('-')[1];

    //When user clicks someone from contact list if theres a channel between users displaying the chat
    try {
        const response = await fetch(`/getPrivateChannel/${parseInt(contactId)}`);
        const channel = await response.json();

        channelIdentificationNumber = channel.channelId;
        $("#current-channel-id").val(channelIdentificationNumber);
        receiverIds = await getArrayOfChannelAsync(channel.channelId);

        $("#chat-display-photo").attr("src", channel.photoDir || "/assets/person-circle.svg");
        $("#chat-display-name").text(channel.channelName);

        const messageRes = await fetch(`/getMessages/${channel.channelId}`);
        const messages = await messageRes.json();
        messages.forEach(displayMessage);

    } catch {
        const userRes = await fetch(`/getUserInfo/${parseInt(contactId)}`);
        const user = await userRes.json();

        $("#chat-display-photo").attr("src", user.profilePhotoPath || "/assets/person-circle.svg");
        $("#chat-display-name").text(user.nickname);

        receiverIds = [user.userId];
        channelIdentificationNumber = '';
        $("#current-channel-id").val(channelIdentificationNumber);
    }
    scrollToBottom();
}

//Retrieving users in the channel to send message
async function getArrayOfChannelAsync(channelId) {
    const response = await fetch(`/getUserArray/${parseInt(channelId)}`);
    return await response.json();
}

//Connecting the web socket
$(function () {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    window.stompClient = stompClient;
    stompClient.connect({}, onConnected, onError);
});

//Subscribing the private messages and handling incoming messages
function onConnected() {
    stompClient.subscribe(`/user/${currentUserId}/messages`, function(message) {
        onMessageReceived(message);
    });

    //Sending the messages also back to us so I can use the message id
    stompClient.subscribe(`/user/${currentUserId}/sendBack`, async (message) => {
        const savedMessage = JSON.parse(message.body);
        const lastMessageId = savedMessage.id;

        if(savedMessage.messageType === "PHOTO"){
            const inputEl = document.getElementById("other-inputs");
            const file = inputEl.files[0];

            const fileDir = await saveFile(file, lastMessageId);
            if (fileDir) {
                chatSection.append($(senderPhoto(fileDir, "", "Just now")));
                $("#other-inputs").val('');
            }
        }else if(savedMessage.messageType === "AUDIO"){
            const audioFile = window.sharedAudio.file;
            const fileDir = await saveFile(audioFile, lastMessageId);

            if (fileDir) {
                chatSection.append($(senderAudio(fileDir, "", "Just now")));
                window.sharedAudio.file = null;
                }
        }

        await findAndDisplayConnectedUsers();
        scrollToBottom();
    });

    stompClient.subscribe(`/user/${currentUserId}/calls`, function(call) {
        window.onCallReceived(call);
    });
}

//When Message Received - Rendering the message
async function onMessageReceived(payload){
    await findAndDisplayConnectedUsers();
    const messageDto = JSON.parse(payload.body);

    if(messageDto.channelId === parseInt(channelIdentificationNumber)){
           const isGroup = channelTypeDisplay === "GROUP";
           let html = '';
           const pp =  messageDto.userDto.profilePhotoPath ||  "/assets/person-circle.svg";
           if (messageDto.messageType === 'TEXT') {
               html = receiverMessage(messageDto.content, messageDto.date, isGroup ? pp : null);
           } else if (messageType === 'PHOTO') {
               html = receiverPhoto(resourceDir, content, date, isGroup ? messageDto.userDto.profilePhotoPath : null);
           } else if (messageType === 'AUDIO'){
              html = receiverAudio(resourceDir, content, date, isGroup ? messageDto.userDto.profilePhotoPath : null);
           }
           chatSection.append($(html));
    }
    scrollToBottom();
}


function onError(error) {
    console.error('WebSocket error:', error);
}

//Sending messages
$("#send-text-message-form").on('submit', (e) => sendMessage(e, 'TEXT'));
$("#file-upload-form").on('submit', (e) => sendMessage(e, 'PHOTO'));
$("#send-button").on('click', (e) => sendMessage(e, 'AUDIO'));

//Function for sending messages
async function sendMessage(event, type) {
    event.preventDefault();

    // Await the new channel creation
    if (channelIdentificationNumber === '') {
        channelIdentificationNumber = await createNewChannelAndGetId();
        $("#current-channel-id").val(channelIdentificationNumber);
    }

    if(type === 'TEXT'){
        const content = $("#message-input").val().trim();

        if (content && stompClient && (channelIdentificationNumber !== '')) {
            stompClient.send("/app/send", {}, JSON.stringify({
                content,
                messageType: type,
                receivers: receiverIds,
                channelId: parseInt(channelIdentificationNumber)
            }));

            $("#message-input").val('');
            chatSection.append($(senderMessage(content, "Just now")));
        }
        scrollToBottom();
        await findAndDisplayConnectedUsers();
    }else {
        var fileName = '';

        if(type === 'AUDIO') {
            const audioFile = window.sharedAudio.file;
            console.log("MESSAGES AUDIO FILE : "+ audioFile);
            if (!audioFile) return;
            fileName = audioFile.name;
        } else {
            //Sending other files
            const inputEl = document.getElementById("other-inputs");
            const file = inputEl.files[0];

            if (!file) return;
            fileName = file.name;
        }

        // Send metadata over WebSocket
        if (fileName && stompClient && channelIdentificationNumber !== '') {
            stompClient.send("/app/send", {}, JSON.stringify({
                content: fileName,
                messageType: type,
                receivers: receiverIds,
                channelId: parseInt(channelIdentificationNumber)
            }));
        }
    }
}

//Creation of new channel if no exists
async function createNewChannelAndGetId() {
    try {
        const receiverId = parseInt(receiverIds[0]);
        const response = await fetch(`/createNewPrivateChannel/${receiverId}`);
        const channelJson = await response.json();

        return channelJson.channelId;
    } catch (error) {
        console.error("Failed to create channel:", error);
        return '';
    }
}

// Saving the file to file structure
async function saveFile(file, messageId) {
    try {
        const channelIdInput = parseInt(channelIdentificationNumber);

        // Prepare form data
        const fileData = new FormData();
        fileData.append("file", file);
        fileData.append("channelId", channelIdInput);
        fileData.append("messageId", messageId);

        // Upload the file
        const uploadResponse = await fetch("/saveFile", {
            method: "POST",
            body: fileData,
        });
        const finalData = await uploadResponse.json();

        return finalData.resourceDir;
    } catch (error) {
        console.error("Something went wrong while uploading the file:", error);
        return null;
    }
}


