document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('.windowOpenBtn');

    buttons.forEach(button => {
        const fileName = button.dataset.arg;
        button.addEventListener('click', () => {
            window.open(fileName, 'mywindow');
        });
    });
});