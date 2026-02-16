// Main JavaScript file for RevHire

document.addEventListener('DOMContentLoaded', () => {
    console.log('RevHire UI Loaded');

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.bg-red-100, .bg-green-100, .alert');
    if (alerts.length > 0) {
        setTimeout(() => {
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    }

    // Check logout link logic - some might prefer form submit for logout
    const logoutLink = document.querySelector('a[href="/logout"]');
    if (logoutLink) {
        logoutLink.addEventListener('click', (e) => {
            // Confirm logic if really needed, but usually redundant for logout
            // if (!confirm('Are you sure you want to log out?')) {
            //     e.preventDefault();
            // }
        });
    }

    // Dynamic File Input Label
    const fileInput = document.getElementById('file');
    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            const fileName = e.target.files[0]?.name;
            const label = e.target.parentElement.querySelector('p');
            if (fileName && label) {
                label.textContent = `Selected: ${fileName}`;
                label.style.fontWeight = 'bold';
                label.style.color = 'var(--primary-color)';
            }
        });
    }
});
