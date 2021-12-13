
const loginForm = document.getElementById("login-form");
const loginButton = document.getElementById("login");
const loginErrorMessage = document.getElementById("error-message");

loginButton.addEventListener("click", (e) => {
    e.preventDefault();
    const username = loginForm.username.value;
    const password = loginForm.password.value;
    loginErrorMessage.value = "success?";

    if (username === "admin" && password === "pass") {
        window.location.href="dashboard.html";
    } else {
        loginErrorMessage.style.opacity = 1;
    }
});


