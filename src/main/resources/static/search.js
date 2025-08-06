const inputElement = document.getElementById("userInput");

inputElement.addEventListener("keydown", function (event) {
    if (event.key === "Enter" && inputElement.value !== "") {
        const query = encodeURIComponent(inputElement.value);
        window.location.href = `serp.html?q=${query}`;
    }
});
