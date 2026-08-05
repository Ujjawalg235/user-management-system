let currentPage = 0;
let pageSize = 5;
let sortBy = 'id';
let sortDirection = 'asc';

const loginContainer = document.getElementById('login-container');
const dashboardContainer = document.getElementById('dashboard-container');
const loginForm = document.getElementById('login-form');
const userTableBody = document.getElementById('user-table-body');
const pageInfo = document.getElementById('page-info');
const btnPrev = document.getElementById('btn-prev');
const btnNext = document.getElementById('btn-next');
const btnSearch = document.getElementById('btn-search');
const btnReset = document.getElementById('btn-reset');
const searchFirstName = document.getElementById('search-first-name');
const searchLastName = document.getElementById('search-last-name');
const btnLogout = document.getElementById('btn-logout');

window.addEventListener('DOMContentLoaded', () => {
    const savedAuth = sessionStorage.getItem('auth');
    if (savedAuth) {
        showDashboard();
    } else {
        showLogin();
    }
});

function showLogin() {
    loginContainer.style.display = 'block';
    dashboardContainer.style.display = 'none';
}

function showDashboard() {
    loginContainer.style.display = 'none';
    dashboardContainer.style.display = 'block';
    fetchUsers();
}

loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;

    const authHash = btoa(usernameInput + ':' + passwordInput);

    fetch('http://localhost:8080/api/users/me', {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + authHash
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Authentication failed');
        }
    })
    .then(data => {
        if (data.success) {
            sessionStorage.setItem('auth', authHash);
            showDashboard();
        } else {
            alert('Login failed: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error logging in:', error);
        alert('Authentication failed. Please check your username and password.');
    });
});

function fetchUsers() {
    const authHash = sessionStorage.getItem('auth');
    if (!authHash) {
        showLogin();
        return;
    }

    let url = `http://localhost:8080/api/users?page=${currentPage}&size=${pageSize}&sortBy=${sortBy}&direction=${sortDirection}`;
    
    const firstName = searchFirstName.value.trim();
    const lastName = searchLastName.value.trim();
    
    if (firstName) {
        url += `&firstName=${encodeURIComponent(firstName)}`;
    }
    if (lastName) {
        url += `&lastName=${encodeURIComponent(lastName)}`;
    }

    fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + authHash
        }
    })
    .then(response => {
        if (response.status === 401 || response.status === 403) {
            showLogin();
            throw new Error('Access denied or unauthorized.');
        }
        return response.json();
    })
    .then(res => {
        if (res.success && res.data) {
            renderTable(res.data);
        } else {
            alert('Failed to load users: ' + res.message);
        }
    })
    .catch(error => {
        console.error('Error fetching users:', error);
    });
}

function renderTable(pageData) {
    userTableBody.innerHTML = '';
    const users = pageData.content || [];

    if (users.length === 0) {
        userTableBody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No users found.</td></tr>';
        pageInfo.textContent = 'Page 0 of 0';
        btnPrev.disabled = true;
        btnNext.disabled = true;
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td>${user.active ? 'Yes' : 'No'}</td>
            <td>
                <button onclick="handleEdit(${user.id})">Edit</button>
                <button onclick="handleDelete(${user.id})">Delete</button>
            </td>
        `;
        userTableBody.appendChild(row);
    });

    const totalPages = pageData.totalPages || 1;
    const currentNum = pageData.number || 0;
    const totalElements = pageData.totalElements || 0;

    pageInfo.textContent = `Page ${currentNum + 1} of ${totalPages} (Total Users: ${totalElements})`;

    btnPrev.disabled = pageData.first;
    btnNext.disabled = pageData.last;
}

btnSearch.addEventListener('click', () => {
    currentPage = 0;
    fetchUsers();
});

btnReset.addEventListener('click', () => {
    searchFirstName.value = '';
    searchLastName.value = '';
    currentPage = 0;
    fetchUsers();
});

btnPrev.addEventListener('click', () => {
    if (currentPage > 0) {
        currentPage--;
        fetchUsers();
    }
});

btnNext.addEventListener('click', () => {
    currentPage++;
    fetchUsers();
});

btnLogout.addEventListener('click', () => {
    sessionStorage.removeItem('auth');
    showLogin();
});

function handleEdit(userId) {
    alert(`Edit functionality for User ID ${userId} is currently in development.`);
}

function handleDelete(userId) {
    alert(`Delete functionality for User ID ${userId} is currently in development.`);
}
