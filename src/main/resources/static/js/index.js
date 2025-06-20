let switchButton = $("#flexSwitchCheckDefault");
let emailForm = $("#email-form");
let phoneForm = $("#phone-form");
let loginForm = $("#login-form");
let nextButton = $(".next-button");
let signinForms = $("#singin-forms");

nextButton.click(function (event) {
    signinForms.addClass("d-none");
    loginForm.removeClass("d-none");
});

switchButton.click(function () {
    if(emailForm.hasClass("d-none")){
        phoneForm.addClass("d-none");
        emailForm.removeClass("d-none");
    }else{
        emailForm.addClass("d-none");
        phoneForm.removeClass("d-none");
    }
});