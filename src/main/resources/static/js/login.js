let switchButton = $("#flexSwitchCheckDefault");
let emailForm = $("#email-form");
let phoneForm = $("#phone-form");
let loginForm = $("#login-form");
let nextButton = $(".next-button");
let signinForms = $("#singin-forms");
let switchVal = $("#switch-val");
let userInfo = $("#user-info");
let email = $("#floatingInput2");
const phone = document.querySelector("#phone");

nextButton.click(function (event) {
    signinForms.addClass("d-none");
    loginForm.removeClass("d-none");
    switchVal.addClass("d-none");

    if (!emailForm.hasClass("d-none")) {
        event.preventDefault();
        fetch('/create-ott', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                username: email.val().trim()
            })
        });
    }else{
        event.preventDefault();
            fetch('/create-ott', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                username: iti.getNumber()
            })
        });
    }
});

switchButton.click(function () {
    if (emailForm.hasClass("d-none")) {
        phoneForm.addClass("d-none");
        emailForm.removeClass("d-none");
    } else {
        emailForm.addClass("d-none");
        phoneForm.removeClass("d-none");
    }
});

let iti = window.intlTelInput(phone, {
    initialCountry: "tr",
    separateDialCode: true,
    strictMode: true,
    loadUtils: () => import("https://cdn.jsdelivr.net/npm/intl-tel-input@25.3.1/build/js/utils.js")
});