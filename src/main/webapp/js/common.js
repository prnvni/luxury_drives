
// Light Mode Toggle
const lightModeBtn = document.getElementById('lightmode');
const body = document.body;

// Check for saved mode preference
const savedMode = localStorage.getItem('lightMode');
if (savedMode === 'enabled') {
    body.classList.add('light-mode');
    lightModeBtn.textContent = 'Dark Mode';
}

lightModeBtn.addEventListener('click', () => {
    body.classList.toggle('light-mode');

    if (body.classList.contains('light-mode')) {
        lightModeBtn.textContent = 'Dark Mode';
        localStorage.setItem('lightMode', 'enabled');
    } else {
        lightModeBtn.textContent = 'Light Mode';
        localStorage.setItem('lightMode', 'disabled');
    }
});
