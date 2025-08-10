/*
	Spring Boot Security REST tutorial 

	https://github.com/egalli64/spring-security-rest
 */
const message = document.getElementById("message");

document.getElementById("login").addEventListener("submit", async (e) => {	
	const username = document.getElementById("username").value;
	const password = document.getElementById("password").value;

	e.preventDefault();
	
	try {
		const response = await fetch("/api/login", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ username, password })
		});

		const data = await response.json();

		if (response.ok) {
			localStorage.setItem("token", data.token);
			message.textContent = "Login successful!";
			setTimeout(() => { message.textContent = "" }, 2000);
		} else {
			message.textContent = "Error: " + data.message;
			setTimeout(() => { message.textContent = "" }, 2000);
		}

	} catch (error) {
		message.textContent = "Network error: " + error.message;
		setTimeout(() => { message.textContent = "" }, 2000);
	}
});

if (localStorage.getItem("token")) {
	document.getElementById("message").textContent = "Already logged in";
	setTimeout(() => { message.textContent = "" }, 2000);
}
