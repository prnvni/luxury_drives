const togglePassword = document.getElementById('togglePassword');

        const password = document.getElementById('password');

        togglePassword.addEventListener('click', function (e) {
            // toggle the type attribute
            const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);
            // toggle the eye icon
            this.classList.toggle('fa-eye-slash');

        });

let loginType = 'user';

userTab.addEventListener('click', () => {
    loginType = 'user';
    userTab.classList.add('active');
    adminTab.classList.remove('active');
    loginTitle.innerText = "USER LOGIN";
    signupLinkContainer.style.display = 'block'; // show sign up for users
});

adminTab.addEventListener('click', () => {
    loginType = 'admin';
    adminTab.classList.add('active');
    userTab.classList.remove('active');
    loginTitle.innerText = "ADMIN LOGIN";
    signupLinkContainer.style.display = 'none'; // hide sign up for admins
});

