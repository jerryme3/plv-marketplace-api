const loginForm = document.querySelector("#loginForm");
const loginButton = document.querySelector("#loginButton");
const message = document.querySelector("#message");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    message.className = "";
    message.textContent = "";
    loginButton.disabled = true;

    const formData = new FormData(loginForm);

    try {
        const response = await fetch("/api/v1/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                email: formData.get("email"),
                password: formData.get("password")
            })
        });

        const responseText = await response.text();

        if (!response.ok) {
            throw new Error(responseText || "Login failed.");
        }

        message.className = "success";
        message.textContent = responseText || "Logged in successfully!";
        
    } catch (error) {
        message.className = "error";
        message.textContent = error.message;
    } finally {
        loginButton.disabled = false;
    }
});
