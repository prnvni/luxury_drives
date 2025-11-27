
const togglePassword = document.getElementById('togglePassword');
const passwordInput = document.getElementById('password');
const emailInput = document.getElementById('usernameEmail');
const loginBtn = document.getElementById('loginBtn'); 
const loginForm = document.getElementById('loginForm');


const userTab = document.getElementById('userTab');
const adminTab = document.getElementById('adminTab');
const loginTitle = document.getElementById('loginTitle');
const signupLinkContainer = document.getElementById('signupLinkContainer');


let loginType = 'user'; // Default to 'user'

// toggle logic (user vs admin) 
if (userTab && adminTab) {
    userTab.addEventListener('click', () => {
        loginType = 'user';
        userTab.classList.add('active');
        adminTab.classList.remove('active');
        if (loginTitle) loginTitle.innerText = "USER LOGIN";
        //show "Sign Up" only for users
        if (signupLinkContainer) signupLinkContainer.style.display = 'block';
    });

    adminTab.addEventListener('click', () => {
        loginType = 'admin';
        adminTab.classList.add('active');
        userTab.classList.remove('active');
        if (loginTitle) loginTitle.innerText = "ADMIN LOGIN";
        // Hide "Sign Up" for admins
        if (signupLinkContainer) signupLinkContainer.style.display = 'none';
    });
}

// toggle for password visibility
if (togglePassword && passwordInput) {
    togglePassword.addEventListener('click', function (e) {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        this.classList.toggle('fa-eye-slash');
    });
}


if (loginForm) {
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const email = emailInput.value;
        const password = passwordInput.value;
        
        if(!email || !password) {
            alert("Please fill in all fields");
            return;
        }

        
        const originalBtnText = loginBtn ? loginBtn.innerText : "Sign In";
        if (loginBtn) {
            loginBtn.innerText = "Checking Credentials...";
            loginBtn.disabled = true;
        }

        //preparing data to send to LoginServlet
        const formData = new URLSearchParams();
        formData.append('email', email);
        formData.append('password', password);
        formData.append('loginType', loginType); //sends 'user' or 'admin'

        //sending request to java backend
       
        fetch("../LoginServlet", { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            if (data.status === "success") {
                console.log("Login Successful:", data);
                

                alert("Welcome, " + data.name + "!");
                
                //redirecting based on role returned by server
                if (data.role === 'admin') {
                    window.location.href = "../html/admin.html";
                } else {
                    window.location.href = "../html/home.html";
                }
            } else {
                //login failed (wrong password/user not found)
                alert(data.message || "Login failed. Please check your credentials.");
                resetButton(originalBtnText);
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("A server error occurred. Please ensure the database is running.");
            resetButton(originalBtnText);
        });
    });
}

function resetButton(text) {
    if (loginBtn) {
        loginBtn.innerText = text;
        loginBtn.disabled = false;
    }
}