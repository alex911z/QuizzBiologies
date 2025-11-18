document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('.nav-button');
    const panels = document.querySelectorAll('.panel');
    const titleEl = document.getElementById('section-title');
    const descriptionEl = document.getElementById('section-description');

    function activateSection(button) {
        const target = button.dataset.target;
        const panelId = `view-${target}`;

        panels.forEach(panel => {
            panel.classList.toggle('is-active', panel.id === panelId);
        });

        buttons.forEach(btn => btn.classList.toggle('is-active', btn === button));

        if (titleEl && button.dataset.title) {
            titleEl.textContent = button.dataset.title;
        }
        if (descriptionEl && button.dataset.description) {
            descriptionEl.textContent = button.dataset.description;
        }
    }

    buttons.forEach(button => {
        button.addEventListener('click', () => activateSection(button));
    });
});
