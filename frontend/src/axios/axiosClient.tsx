import axios from "axios";

const client = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
});

client.defaults.xsrfCookieName = 'XSRF-TOKEN';
client.defaults.xsrfHeaderName = 'X-XSRF-TOKEN';

client.interceptors.request.use(async (config) => {
    if (!document.cookie.includes('XSRF-TOKEN')) {
        await axios.post('/api/csrf', {}, {withCredentials: true});
    }
    return config;
});

export default client;