$( window ).on( "load", findAndDisplayConnectedUsers);

//Displaying Messaged Users
async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/getChannels');
    let registeredChannelDtos = await connectedUsersResponse.json();

    const channelsList = $("#channels");
    channelsList.empty();

    registeredChannelDtos.forEach(chnl => {
        const channelPhotoPath = chnl.photoDir || '/assets/person-circle.svg';
        const name = chnl.channelName;
        const channelId = chnl.channelId;
        const lastMessage = (chnl.lastMessage != null) ? chnl.lastMessage : "";
        const lastUpdate = chnl.date;
        const channelType = chnl.channelType;
        const givenId = channelType + "-" + channelId;

        const $a = $(`
            <a class="list-group-item list-group-item-action py-3 lh-sm single-user" aria-current="true" style="width: 30rem;" id="${givenId}">
                <div class="d-flex">
                    <img src="${channelPhotoPath}" alt="" style="height: 3rem; width: 3rem; margin-right: 1rem; object-fit: cover;" class="rounded-circle">
                    <div style="width: 100%;">
                        <div class="d-flex w-100 align-items-center justify-content-between">
                            <strong class="mb-1 text-truncate" style="max-width: 15rem; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">${name}</strong>
                            <small>
                                ${lastUpdate}
                            </small>
                        </div>
                        <div class="small text-truncate" style="max-width: 22rem; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">${lastMessage}</div>
                    </div>
                </div>
            </a>
        `);

        channelsList.append($a);
    });
}

//Click on a user
let channelIdentificationNumber;
let channelTypeDisplay;
$("#channels").on("click", ".single-user", function () {
    if (!$("#info-page").hasClass("d-none")) {
        $("#info-page").addClass("d-none");
    }
    if ($("#user-chat").hasClass("d-none")) {
        $("#user-chat").removeClass("d-none");
    }

    channelIdentificationNumber = $(this).attr('id').split('-')[1];
    channelTypeDisplay = $(this).attr('id').split('-')[0];
    fetchAndDisplayUserChat();
});

//Display upper part
async function fetchAndDisplayUserOrGroupPhotos (targetId, chnlType) {
    const response = await fetch(`/getChannelInfo/${parseInt(targetId)}`);
    const channelDto = await response.json();

    var channelName = channelDto.channelName;
    let pp;
    if(chnlType === "GROUP"){
        pp = channelDto.photoDir || "/assets/people-fill.svg";
    }else{
        pp = channelDto.photoDir || "/assets/person-circle.svg";
    }

    $("#chat-display-photo").attr("src", pp);
    $("#chat-display-name").text(channelName);
}

//Displaying the messages
let chatSection = $(".chat-area");
let currentUserId = 1; //!!use something dynamic later

async function fetchAndDisplayUserChat() {
    fetchAndDisplayUserOrGroupPhotos(channelIdentificationNumber, channelTypeDisplay);

    chatSection.empty();
    const chatResponse = await fetch(`/getMessages/${parseInt(channelIdentificationNumber)}`);
    const messageJsonList = await chatResponse.json();

    messageJsonList.forEach(message => {

        var photo = message.resourceDir;
        var messageDate = message.date;
        var messageCnt = message.content;
        var pp = message.userDto.profilePhotoPath || "/assets/person-circle.svg";

        //If it is a group message display user photos and usernames
        if(channelTypeDisplay === "GROUP"){
            //if message sent by others
            if (!(message.userDto.userId === parseInt(currentUserId))) {
                    if (message.messageType === 'TEXT') {
                         const $div = $(`
                            <div class="chat-message receiver">
                                <img src="${pp}" alt="profile-picture" class="rounded-circle mt-1" height="40" width="40" style="object-fit: cover;"/>
                                <div class="message mx-1 mt-2">
                                    <p style="margin-bottom: -5px;">${messageCnt}</p>
                                    <div class=" d-flex justify-content-end">
                                        <small style="color: grey; font-size: 12px;">${messageDate}</small>
                                    </div>
                                </div>
                            </div>
                        `);
                        chatSection.append($div);
                    }else if(message.messageType === 'PHOTO'){
                         const $div = $(`
                            <div class="chat-message receiver">
                                <img src="${pp}" alt="profile-picture" class="rounded-circle mt-1" height="40" width="40" style="object-fit: cover;"/>
                                <div class="message mx-1 mt-2">
                                    <img class="rounded-3" src="${pp}" alt="pp" style="width: 20rem; height: 20rem; object-fit: cover;">
                                    <p style="margin-bottom: -5px;">${messageCnt}</p>
                                    <div class=" d-flex justify-content-end">
                                        <small style="color: grey; font-size: 12px;">${messageDate}</small>
                                    </div>
                                </div>
                            </div>
                        `);
                        chatSection.append($div);
                    }
            } else {
                  if (message.messageType === 'TEXT') {
                      const $div = $(`
                          <div class="chat-message sender mx-4 mt-2">
                              <div class="message">
                                  <p style="margin-bottom: -10px;">${messageCnt}</p>
                                  <small style="color: grey; font-size: 12px;">${messageDate}</small>
                              </div>
                          </div>
                      `);
                      chatSection.append($div);
                  } else if(message.messageType === 'PHOTO') {
                      const $div = $(`
                          <div class="chat-message sender mx-4 mt-2">
                              <div class="message ">
                                  <img class="rounded-3" src="${photo}" alt="" style="width: 20rem; height: 20rem; object-fit: cover;">
                                  <p style="margin-bottom: -10px;">${messageCnt}</p>
                                  <small style="color: grey; font-size: 12px;">${messageDate}</small>
                              </div>
                          </div>
                      `);
                      chatSection.append($div);
                  }
            }
        }else{
            //If message sent by our contact
            if (!(message.userDto.userId === parseInt(currentUserId))) {

                if (message.messageType === 'TEXT') {
                    const $div = $(`
                        <div class="chat-message receiver">
                            <div class="message mx-4 mt-2">
                                <p style="margin-bottom: -5px;">${messageCnt}</p>
                                <div class=" d-flex justify-content-end">
                                    <small style="color: grey; font-size: 12px;">${messageDate}</small>
                                </div>
                            </div>
                        </div>
                    `);
                    chatSection.append($div);
                } else if(message.messageType === 'PHOTO'){
                    const $div = $(`
                        <div class="chat-message receiver">
                            <div class="message mx-4 mt-2">
                                <img class="rounded-3" src="${photo}" alt="" style="width: 20rem; height: 20rem; object-fit: cover;">
                                <p style="margin-bottom: -5px;">${messageCnt}</p>
                                <div class=" d-flex justify-content-end">
                                    <small style="color: grey; font-size: 12px;">${messageDate}</small>
                                </div>
                            </div>
                        </div>
                    `);
                    chatSection.append($div);
                }
            } else {
                if (message.messageType === 'TEXT') {
                    const $div = $(`
                        <div class="chat-message sender mx-4 mt-2">
                            <div class="message">
                                <p style="margin-bottom: -10px;">${messageCnt}</p>
                                <small style="color: grey; font-size: 12px;">${messageDate}</small>
                            </div>
                        </div>
                    `);
                    chatSection.append($div);
                } else if(message.messageType === 'PHOTO') {
                    const $div = $(`
                        <div class="chat-message sender mx-4 mt-2">
                            <div class="message ">
                                <img class="rounded-3" src="${messageDate}" alt="" style="width: 20rem; height: 20rem; object-fit: cover;">
                                <p style="margin-bottom: -10px;">${messageCnt}</p>
                                <small style="color: grey; font-size: 12px;">${messageDate}</small>
                            </div>
                        </div>
                    `);
                    chatSection.append($div);
                }
            }
        }
    });

    $(".chat-area").scrollTop($(".chat-area")[0].scrollHeight);
}