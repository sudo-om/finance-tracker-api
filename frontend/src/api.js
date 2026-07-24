// Dynamic API Base URL: Uses environment variable VITE_API_BASE_URL if set, or defaults to localhost:8081
const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (!envUrl) return 'http://localhost:8081';
  let url = envUrl.trim().replace(/\/$/, '');
  if (!url.startsWith('http://') && !url.startsWith('https://')) {
    url = `https://${url}`;
  }
  return url;
};

export const BASE_URL = getBaseUrl();

export const getAuthToken = () => {
  const token = localStorage.getItem('token');
  if (!token || token === 'null' || token === 'undefined') return null;
  return token;
};

export const setAuthToken = (token) => {
  if (token && token !== 'null' && token !== 'undefined') {
    localStorage.setItem('token', token);
  } else {
    localStorage.removeItem('token');
  }
};

export const removeAuthToken = () => localStorage.removeItem('token');
export const getUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};
export const setUser = (user) => localStorage.setItem('user', JSON.stringify(user));

const headers = () => {
  const token = getAuthToken();
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

async function request(endpoint, options = {}) {
  const targetUrl = `${BASE_URL}${endpoint}`;
  try {
    const res = await fetch(targetUrl, {
      ...options,
      headers: {
        ...headers(),
        ...(options.headers || {}),
      },
    });

    if (res.status === 204) return true;

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      throw new Error(data.message || data.error || `Request failed (${res.status})`);
    }

    // Automatically unwrap Spring Boot ApiResponse wrapper if present
    if (data && typeof data === 'object' && 'data' in data && data.data !== null && data.data !== undefined) {
      return data.data;
    }

    return data;
  } catch (err) {
    if (err.name === 'TypeError' && err.message === 'Failed to fetch') {
      throw new Error(`Unable to connect to backend API (${BASE_URL}). If using Render free tier, the backend service may be waking up from sleep. Please try again in 20-30 seconds.`);
    }
    throw err;
  }
}

export const api = {
  login: (email, password) =>
    request('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (firstName, lastName, email, password) =>
    request('/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify({ firstName, lastName, email, password }),
    }),

  getCategories: () => request('/api/v1/categories'),
  getBalance: () => request('/api/v1/dashboard/balance'),
  getSummary: () => request('/api/v1/dashboard/summary'),

  getExpenses: (page = 0, size = 20) => request(`/api/v1/expenses?page=${page}&size=${size}`),
  createExpense: (data) =>
    request('/api/v1/expenses', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  deleteExpense: (id) =>
    request(`/api/v1/expenses/${id}`, {
      method: 'DELETE',
    }),

  getIncomes: (page = 0, size = 20) => request(`/api/v1/incomes?page=${page}&size=${size}`),
  createIncome: (data) =>
    request('/api/v1/incomes', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  deleteIncome: (id) =>
    request(`/api/v1/incomes/${id}`, {
      method: 'DELETE',
    }),

  getBudgets: () => request('/api/v1/budgets'),
  createBudget: (data) =>
    request('/api/v1/budgets', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  deleteBudget: (id) =>
    request(`/api/v1/budgets/${id}`, {
      method: 'DELETE',
    }),

  generateTelegramLinkCode: () =>
    request('/api/v1/telegram/link-code', {
      method: 'POST',
    }),
};
