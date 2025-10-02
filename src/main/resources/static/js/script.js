document.addEventListener('DOMContentLoaded', function () {
    const toasts = document.querySelectorAll('.toast');
    toasts.forEach(function (toastEl) {
        const toast = new bootstrap.Toast(toastEl, {
            autohide: true,
            delay: 5000
        });
        toast.show();
    });

    document.querySelectorAll('.open-rename-modal').forEach(button => {
        button.addEventListener('click', () => {
            const fileId = button.getAttribute('data-file-id');
            const fileName = button.getAttribute('data-file-name');

            document.getElementById('modalRenameId').value = fileId;
            document.getElementById('modalRenameName').value = fileName;

            const modal = new bootstrap.Modal(document.getElementById('renameFileModal'));
            modal.show();
        });
    });

    document.querySelectorAll('.open-delete-modal').forEach(button => {
        button.addEventListener('click', () => {
            document.getElementById('modalDeleteId').value = button.getAttribute('data-file-id');
            const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
            modal.show();
        });
    });

    const fileInfoModal = document.getElementById('fileInfoModal');
    if (fileInfoModal) {
        const modal = new bootstrap.Modal(fileInfoModal);
        modal.show();

        fileInfoModal.addEventListener('hidden.bs.modal', () => {
            const video = fileInfoModal.querySelector('#fileInfoVideo');
            if (video) {
                video.pause();
                video.currentTime = 0;
            }
        });
    }

    const btn = document.querySelector('.theme-toggle');
    const body = document.body;

    let theme = localStorage.getItem('theme') || 'dark';
    body.dataset.bsTheme = theme;
    btn.innerHTML = theme === 'light' ? '<i class="bi bi-moon"></i>' : '<i class="bi bi-sun"></i>';

    btn.addEventListener('click', () => {
        theme = theme === 'light' ? 'dark' : 'light';
        body.dataset.bsTheme = theme;
        localStorage.setItem('theme', theme);
        btn.innerHTML = theme === 'light' ? '<i class="bi bi-moon"></i>' : '<i class="bi bi-sun"></i>';
    });
});
