/*
	Spring Boot Security REST tutorial 

	https://github.com/egalli64/spring-security-rest
 */
const result = document.getElementById("result");

document.getElementById("loadData").addEventListener("click", async () => {
	try {
		const token = localStorage.getItem("token");
		const response = await fetch("/api/admin", { headers: { "Authorization": "Bearer " + token } });

		if (response.ok) {
			const data = await response.text();
			result.textContent = "Data: " + data;
		} else if (response.status === 401) {
			result.textContent = "Token expired or invalid. Please, login.";
			localStorage.removeItem("token");
			document.getElementById("username").focus();
			setTimeout(() => { result.textContent = "" }, 2000);
		} else {
			result.textContent = "Error: " + response.status;
		}
	} catch (error) {
		result.textContent = "Network error: " + error.message;
	}
});

document.getElementById("checkToken").addEventListener("click", async () => {
	try {
		const token = localStorage.getItem("token");
		const response = await fetch("/api/token/status", { headers: { "Authorization": "Bearer " + token } });

		if (response.ok) {
			const data = await response.json();
			result.textContent = "Token expiration: " + new Date(data.expiration);
		} else {
			const error = await response.json();
			result.textContent = "Error: " + error.message;
		}
	} catch (error) {
		result.textContent = "Error checking token: " + error.message;
	}
});

document.getElementById("logout").addEventListener("click", () => {
	localStorage.removeItem("token");
	result.textContent = "Logged out successfully!";
	document.getElementById("username").focus();
	setTimeout(() => { result.textContent = "" }, 2000);
});
