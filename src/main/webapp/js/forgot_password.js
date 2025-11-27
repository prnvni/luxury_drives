const form = document.getElementById('forgotPasswordForm');
const emailInput = document.getElementById('email');
const passwordGroup = document.getElementById('newPasswordGroup');
const newPasswordInput = document.getElementById('newPassword');
const submitBtn = document.getElementById('submitBtn');

let step = 1; //step 1: verify email 

form.addEventListener('submit', (e) => {
    e.preventDefault();
    
    const email = emailInput.value;

    if (step === 1) {
        //step 1
        submitBtn.innerText = "Checking...";
        
        fetch("../ForgotPasswordServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "action=check&email=" + encodeURIComponent(email)
        })
        .then(res => res.json())
        .then(data => {
            if (data.status === "success") {
                alert("Email found! Please enter your new password.");
                
                step = 2;
                emailInput.disabled = true; //locking email field
                passwordGroup.classList.remove('d-none'); //showing password field
                newPasswordInput.required = true;
                submitBtn.innerText = "Update Password";
            } else {
                alert("Error: " + data.message);
                submitBtn.innerText = "Find Account";
            }
        });

    } else if (step === 2) {
        //step 2: update password
        const newPass = newPasswordInput.value;
        submitBtn.innerText = "Updating...";

        fetch("../ForgotPasswordServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "action=update&email=" + encodeURIComponent(email) + "&password=" + encodeURIComponent(newPass)
        })
        .then(res => res.json())
        .then(data => {
            if (data.status === "success") {
                alert("Password updated successfully! You can now login.");
                window.location.href = "login.html";
            } else {
                alert("Error: " + data.message);
                submitBtn.innerText = "Update Password";
            }
        });
    }
});