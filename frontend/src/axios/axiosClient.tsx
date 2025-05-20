import axios from "axios";

const client = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
});

type AuthType = 'jwt' | 'session' | 'oauth' | null;
let authType: AuthType = null;

const setupJwtAuth = () => {
    client.interceptors.request.use(config => {
        const token = localStorage.getItem('jwt_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });
};

const setupSessionAuth = () => {
    client.defaults.xsrfCookieName = 'XSRF-TOKEN';
    client.defaults.xsrfHeaderName = 'X-XSRF-TOKEN';

    client.interceptors.request.use(async (config) => {
        if (!document.cookie.includes('XSRF-TOKEN')) {
            await axios.get('/api/csrf', {});
        }
        return config;
    });
};

client.interceptors.request.use(async (config) => {
    if (!document.cookie.includes('AuthType')) {
        await axios.get('/api/config', {withCredentials: true});
        const authCookie = /AuthType=([^;]+)/.exec(document.cookie);
        authType = authCookie ? authCookie[1] as AuthType : null;

        if (authType === 'jwt') setupJwtAuth();
        else if (authType === 'session') setupSessionAuth();
        else {
            console.error("error fetching config cookie?")
            return config;
        }
    }
    return config;
});


export default client;
export const getAuthType = () => authType;