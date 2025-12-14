// DriveSmart App JavaScript
class DriveSmartApp {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupSwipeGestures();
        this.setupServiceWorker();
        this.updateOnlineStatus();
    }

    // Sidebar
    toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
        document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
    }

    // User Menu
    toggleUserMenu() {
        const userMenu = document.getElementById('userMenu');
        const overlay = document.getElementById('overlay');
        userMenu.classList.toggle('active');
        overlay.classList.toggle('active');
    }

    // FAB Menu
    toggleFabMenu() {
        const fabMenu = document.getElementById('fabMenu');
        const fabIcon = document.getElementById('fabIcon');
        fabMenu.classList.toggle('active');
        fabIcon.textContent = fabMenu.classList.contains('active') ? '✕' : '➕';
    }

    // Close all modals
    closeAllModals() {
        document.getElementById('sidebar').classList.remove('active');
        document.getElementById('userMenu').classList.remove('active');
        document.getElementById('fabMenu').classList.remove('active');
        document.getElementById('overlay').classList.remove('active');
        document.body.style.overflow = '';
    }

    // Notifications
    showNotifications() {
        // Fetch notifications from backend
        fetch('/api/notifications')
            .then(response => response.json())
            .then(data => this.displayNotifications(data))
            .catch(error => console.error('Error fetching notifications:', error));
    }

    displayNotifications(notifications) {
        // Create notification dropdown
        const dropdown = document.createElement('div');
        dropdown.className = 'notification-dropdown';
        dropdown.innerHTML = `
            <div class="notification-header">
                <h3>Notifications</h3>
                <button onclick="this.parentElement.parentElement.remove()">✕</button>
            </div>
            <div class="notification-list">
                ${notifications.map(n => `
                    <div class="notification-item ${n.unread ? 'unread' : ''}">
                        <div class="notification-icon">${n.icon || '🔔'}</div>
                        <div class="notification-content">
                            <div class="notification-title">${n.title}</div>
                            <div class="notification-time">${this.formatTime(n.time)}</div>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
        
        document.body.appendChild(dropdown);
        setTimeout(() => dropdown.remove(), 5000);
    }

    // Swipe gestures for mobile
    setupSwipeGestures() {
        let touchStartX = 0;
        let touchStartY = 0;

        document.addEventListener('touchstart', (e) => {
            touchStartX = e.touches[0].clientX;
            touchStartY = e.touches[0].clientY;
        });

        document.addEventListener('touchend', (e) => {
            const touchEndX = e.changedTouches[0].clientX;
            const touchEndY = e.changedTouches[0].clientY;
            const diffX = touchStartX - touchEndX;
            const diffY = touchStartY - touchEndY;

            // Horizontal swipe (close sidebar)
            if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 50) {
                if (diffX > 0) {
                    // Swipe left - close sidebar if open
                    if (document.getElementById('sidebar').classList.contains('active')) {
                        this.closeAllModals();
                    }
                }
            }
        });
    }

    // Service Worker for PWA
    setupServiceWorker() {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/sw.js')
                .then(registration => {
                    console.log('Service Worker registered:', registration);
                })
                .catch(error => {
                    console.error('Service Worker registration failed:', error);
                });
        }
    }

    // Online/Offline status
    updateOnlineStatus() {
        const status = navigator.onLine ? 'online' : 'offline';
        document.body.setAttribute('data-connection', status);
        
        window.addEventListener('online', () => {
            this.showToast('You are back online', 'success');
            document.body.setAttribute('data-connection', 'online');
        });

        window.addEventListener('offline', () => {
            this.showToast('You are offline', 'warning');
            document.body.setAttribute('data-connection', 'offline');
        });
    }

    // Toast notifications
    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 1rem;
            right: 1rem;
            padding: 1rem 1.5rem;
            border-radius: var(--radius-sm);
            background: ${this.getToastColor(type)};
            color: white;
            box-shadow: var(--shadow-lg);
            z-index: 10000;
            animation: slideInRight 0.3s ease;
        `;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3000);

        // Add animation if not exists
        if (!document.querySelector('#toast-animations')) {
            const style = document.createElement('style');
            style.id = 'toast-animations';
            style.textContent = `
                @keyframes slideInRight {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOutRight {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            `;
            document.head.appendChild(style);
        }
    }

    getToastColor(type) {
        const colors = {
            success: '#4CD964',
            error: '#FF3B30',
            warning: '#FF9500',
            info: '#5AC8FA'
        };
        return colors[type] || colors.info;
    }

    // Format time
    formatTime(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;
        
        if (diff < 60000) return 'Just now';
        if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
        return date.toLocaleDateString();
    }

    // Form validation
    validateForm(form) {
        const inputs = form.querySelectorAll('[required]');
        let isValid = true;

        inputs.forEach(input => {
            if (!input.value.trim()) {
                input.classList.add('error');
                isValid = false;
            } else {
                input.classList.remove('error');
            }
        });

        return isValid;
    }

    // API helper
    async apiRequest(endpoint, method = 'GET', data = null) {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(endpoint, options);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API Request failed:', error);
            this.showToast('Network error. Please try again.', 'error');
            throw error;
        }
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.app = new DriveSmartApp();
    
    // Close modals on escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            app.closeAllModals();
        }
    });
    
    // Prevent body scroll when modal is open
    document.addEventListener('touchmove', (e) => {
        if (document.getElementById('overlay').classList.contains('active')) {
            e.preventDefault();
        }
    }, { passive: false });
});