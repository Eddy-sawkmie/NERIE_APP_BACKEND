/**
 * Layout modes: 'sidebar' | 'topbar'
 * Preference is persisted in localStorage under key 'navLayoutMode'.
 */
const NAV_MODE_KEY = 'navLayoutMode';
const NAV_MODE_SIDEBAR = 'sidebar';
const NAV_MODE_TOPBAR = 'topbar';

/**
 * Called once on page load — reads saved preference and applies it.
 */
function applyNavModeOnLoad() {
    // On small screens (<1024px) always use sidebar mode regardless of saved preference
    if (window.innerWidth < 1024) {
        setNavMode(NAV_MODE_SIDEBAR, false);
        return;
    }
    const saved = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;
    setNavMode(saved, false);
}

/**
 * Sets the navigation mode and optionally persists the preference.
 * @param {'sidebar'|'topbar'} mode
 * @param {boolean} persist - whether to save to localStorage
 */
function setNavMode(mode, persist = true) {
    document.body.classList.remove('nav-mode-sidebar', 'nav-mode-topbar');
    document.body.classList.add(`nav-mode-${mode}`);

    if (persist) {
        localStorage.setItem(NAV_MODE_KEY, mode);
    }

    updateToggleButtonLabel(mode);

    // On mode switch, re-run submenu matching so active states render correctly
    matchMenuWithLink();

    // If switching to topbar while mobile sidebar is open, close it
    if (mode === NAV_MODE_TOPBAR) {
        const sidebar = document.getElementById('dynamicmenu');
        if (sidebar) {
            sidebar.classList.remove('sidebar-open');
            document.body.classList.remove('sidebar-active-lock');
        }
    }
}

/**
 * Toggle between sidebar and topbar modes.
 */
function toggleNavLayout() {
    const current = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;
    const next = current === NAV_MODE_SIDEBAR ? NAV_MODE_TOPBAR : NAV_MODE_SIDEBAR;
    setNavMode(next);

    // Close any open submenus when toggling
    closeAllSubmenusExceptTarget(null);
}

/**
 * Updates the toggle button icon and label to reflect the NEXT possible mode.
 */
function updateToggleButtonLabel(currentMode) {
    const btn = document.getElementById('layout-toggle-btn');
    if (!btn) return;

    if (currentMode === NAV_MODE_SIDEBAR) {
        // Currently sidebar → offer switch to topbar
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M0 3.5A1.5 1.5 0 0 1 1.5 2h13A1.5 1.5 0 0 1 16 3.5v2A1.5 1.5 0 0 1 14.5 7h-13A1.5 1.5 0 0 1 0 5.5zM1.5 3a.5.5 0 0 0-.5.5v2a.5.5 0 0 0 .5.5h13a.5.5 0 0 0 .5-.5v-2a.5.5 0 0 0-.5-.5z"/>
                <path d="M0 10.5A1.5 1.5 0 0 1 1.5 9h5A1.5 1.5 0 0 1 8 10.5v2A1.5 1.5 0 0 1 6.5 14h-5A1.5 1.5 0 0 1 0 12.5zm1.5-.5a.5.5 0 0 0-.5.5v2a.5.5 0 0 0 .5.5h5a.5.5 0 0 0 .5-.5v-2a.5.5 0 0 0-.5-.5z"/>
            </svg>
            Switch to Navbar`;
    } else {
        // Currently topbar → offer switch to sidebar
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M0 3h16v2H0zm0 4h4v2H0zm0 4h4v2H0zm5-8h11v2H5zm0 4h11v2H5zm0 4h11v2H5z"/>
            </svg>
            Switch to Sidebar`;
    }
}

/**
 * Main entry point called after sidebar HTML is injected.
 */
function initializeSidebar() {
    applyNavModeOnLoad();
    setupAllEventListeners();
    // matchMenuWithLink is already called inside applyNavModeOnLoad → setNavMode
}

/**
 * Sets up all event listeners.
 */
function setupAllEventListeners() {
    const sidebar = document.getElementById('dynamicmenu');
    const hamburgerBtn = document.getElementById('hamburger-btn');
    const closeBtn = document.getElementById('sidebar-close-btn');

    if (!sidebar) return;

    // --- Mobile Sidebar Controls ---
    const closeMobileSidebar = () => {
        sidebar.classList.remove('sidebar-open');
        document.body.classList.remove('sidebar-active-lock');
        if (hamburgerBtn) {
            hamburgerBtn.classList.remove('is-active');
            hamburgerBtn.setAttribute('aria-expanded', 'false');
        }
    };

    if (hamburgerBtn) {
        hamburgerBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            sidebar.classList.toggle('sidebar-open');
            hamburgerBtn.classList.toggle('is-active');
            const isOpen = sidebar.classList.contains('sidebar-open');
            hamburgerBtn.setAttribute('aria-expanded', String(isOpen));
            document.body.classList.toggle('sidebar-active-lock', isOpen);
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            closeMobileSidebar();
        });
    }

    // --- Close sidebar on outside click (mobile) ---
    document.addEventListener('click', e => {
        if (window.innerWidth <= 991.98 && sidebar.classList.contains('sidebar-open')) {
            if (!sidebar.contains(e.target) && hamburgerBtn && !hamburgerBtn.contains(e.target)) {
                closeMobileSidebar();
            }
        }
    });

    // --- Close open topbar dropdowns on outside click ---
    document.addEventListener('click', e => {
        const currentMode = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;
        if (currentMode === NAV_MODE_TOPBAR) {
            if (!e.target.closest('#dynamicmenu')) {
                closeAllSubmenusExceptTarget(null);
            }
        }
    });

    window.addEventListener('resize', () => {
        if (window.innerWidth < 1024) {
            // Auto-switch to sidebar mode on small screens
            setNavMode(NAV_MODE_SIDEBAR, false);
        } else {
            closeMobileSidebar();
            // Restore saved preference when going back to desktop
            const saved = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;
            setNavMode(saved, false);
        }
    });
}

/**
 * Finds the link matching the current URL, marks it active, and opens its submenu.
 */
function matchMenuWithLink() {
    const sidebar = document.getElementById('dynamicmenu');
    if (!sidebar) return;

    // Clear existing active states first
    sidebar.querySelectorAll('.active-menu, .active-sub-link').forEach(el => {
        el.classList.remove('active-menu', 'active-sub-link');
    });

    const allLinks = sidebar.querySelectorAll('a');
    let pathToMatch = window.location.pathname;

    const activeMenuMeta = document.querySelector('meta[name="active-menu"]');
    if (activeMenuMeta && activeMenuMeta.content) {
        pathToMatch = activeMenuMeta.content;
    }

    const activeLink = Array.from(allLinks).find(link => link.getAttribute('href') === pathToMatch);
    if (!activeLink) return;

    const parentSubmenu = activeLink.closest('.nav-item-submenu');
    const currentMode = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;

    if (parentSubmenu) {
        const menuButton = parentSubmenu.previousElementSibling;
        if (menuButton) {
            menuButton.classList.add('active-menu');

            if (currentMode === NAV_MODE_SIDEBAR) {
                // Sidebar: highlight the child link AND open the submenu
                activeLink.classList.add('active-sub-link');
                if (!menuButton.classList.contains('menu-open')) {
                    toggleSubmenuDisplay(parentSubmenu, menuButton);
                }
            }
            // Topbar: only highlight the parent button, leave dropdown closed
        }
    } else if (activeLink.classList.contains('nav-link-menu')) {
        activeLink.classList.add('active-menu');
    }
}

/**
 * Expands/collapses a submenu on parent link click.
 */
function expandSubmenu(event) {
    event.preventDefault();
    event.stopPropagation(); // Prevent the document outside-click handler from closing immediately
    const menuButton = event.currentTarget;
    const submenu = menuButton.nextElementSibling;

    if (submenu && submenu.classList.contains('nav-item-submenu')) {
        if (!menuButton.classList.contains('menu-open')) {
            closeAllSubmenusExceptTarget(submenu);
        }
        toggleSubmenuDisplay(submenu, menuButton);
    }
}

/**
 * Closes all submenus except the target one.
 * Pass null to close all.
 */
function closeAllSubmenusExceptTarget(targetSubmenu) {
    document.querySelectorAll('.nav-item-submenu').forEach(submenu => {
        if (submenu !== targetSubmenu) {
            submenu.style.display = 'none';
            const menuButton = submenu.previousElementSibling;
            if (menuButton) menuButton.classList.remove('menu-open');
        }
    });
}

/**
 * Toggles a single submenu open/closed.
 * In topbar mode, positions the dropdown using fixed coords from getBoundingClientRect()
 * so it is never clipped by an overflow ancestor.
 */
function toggleSubmenuDisplay(submenu, menuButton) {
    const isOpening = !menuButton.classList.contains('menu-open');
    if (isOpening) {
        submenu.style.display = 'flex';
        menuButton.classList.add('menu-open');

        const currentMode = localStorage.getItem(NAV_MODE_KEY) || NAV_MODE_SIDEBAR;
        if (currentMode === NAV_MODE_TOPBAR) {
            positionDropdown(submenu, menuButton);
        }
    } else {
        submenu.style.display = 'none';
        menuButton.classList.remove('menu-open');
    }
}

// Track the currently open dropdown + its trigger button for scroll repositioning
let _activeDropdown = null;
let _activeDropdownBtn = null;

/**
 * Positions a dropdown using fixed coordinates derived from the trigger button's
 * bounding rect. This escapes all overflow:auto/hidden ancestors.
 */
function positionDropdown(submenu, menuButton) {
    const rect = menuButton.getBoundingClientRect();
    submenu.style.top  = (rect.bottom + 4) + 'px';
    submenu.style.left = rect.left + 'px';

    // Ensure dropdown does not overflow the right edge of the viewport
    requestAnimationFrame(() => {
        const dropRect = submenu.getBoundingClientRect();
        if (dropRect.right > window.innerWidth - 8) {
            submenu.style.left = (window.innerWidth - dropRect.width - 8) + 'px';
        }
    });

    // Track so the scroll listener can reposition it
    _activeDropdown = submenu;
    _activeDropdownBtn = menuButton;
}

/**
 * On scroll, reposition the open dropdown so it stays anchored to its button.
 * If the button scrolls off screen entirely, close the dropdown.
 */
document.addEventListener('scroll', () => {
    if (!_activeDropdown || !_activeDropdownBtn) return;
    if (_activeDropdown.style.display !== 'flex') return;

    const rect = _activeDropdownBtn.getBoundingClientRect();
    if (rect.bottom < 0 || rect.top > window.innerHeight) {
        closeAllSubmenusExceptTarget(null);
    } else {
        _activeDropdown.style.top  = (rect.bottom + 4) + 'px';
        _activeDropdown.style.left = rect.left + 'px';
    }
}, { passive: true });