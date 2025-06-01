import axios, {type AxiosError, type AxiosRequestConfig} from "axios";
import type {APIResponse, AuthType, UserData} from "../types/APIResponse.tsx";
import type {UserDTO} from "../types/UserDTO.tsx";

export const client = axios.create({
    baseURL: "/api",
    withCredentials: true,
    headers: {
        "Content-Type": 'application/json'
    }
});

let jwtToken: string | null = null;

export const setJwtToken = (token: string) => {
    jwtToken = token;
    client.defaults.headers.common.Authorization = `Bearer ${token}`;
};

export const clearJwtToken = () => {
    jwtToken = null;
    delete client.defaults.headers.common.Authorization;
};

export const getJwtToken = () => jwtToken;

async function requestWithData<T>(config: AxiosRequestConfig): Promise<T> {
    try {
        const response = await client.request<APIResponse<T>>(config);

        if (response.data.data === undefined || response.data.data === null)
            throw new Error("Request succeeded, malformed or missing data");

        return response.data.data;
    } catch (e) {
        const axiosError = e as AxiosError<APIResponse>;

        if (axiosError.response) {
            throw new Error(axiosError.response.data.message);
        } else
            throw new Error("Network error");
    }
}

async function requestWithoutData(config: AxiosRequestConfig): Promise<string> {
    try {
        const response = await client.request<APIResponse>(config);
        return response.data.message;
    } catch (e) {
        const axiosError = e as AxiosError<APIResponse>;

        if (axiosError.response) {
            throw new Error(axiosError.response.data.message);
        } else
            throw new Error("Other error");
    }
}

export async function getConfig(): Promise<AuthType> {
    return requestWithData<AuthType>({
        method: "GET",
        url: "/config"
    })
}

export async function getUser(): Promise<UserData> {
    return requestWithData<UserData>({
        method: "GET",
        url: "/user"
    })
}

export async function getProtected(): Promise<UserData> {
    return requestWithData<UserData>({
        method: "GET",
        url: "/protected"
    })
}

export async function login(user: UserDTO): Promise<UserData> {
    const response = await requestWithData<UserData & { jwt?: string }>({
        method: "POST",
        url: "/login",
        data: user
    });

    if ('jwt' in response && response.jwt) {
        setJwtToken(response.jwt);
    }

    return response;
}

export async function logout(): Promise<string> {
    return requestWithoutData({
        method: "POST",
        url: "/logout"
    })
}

const hasCsrfToken = () =>
    document.cookie.split(";").some(cookie => cookie.trim().startsWith("XSRF-TOKEN="));

export async function init() {
    const config = await getConfig();

    if (config === 'session') {
        client.defaults.xsrfCookieName = "XSRF-TOKEN";
        client.defaults.xsrfHeaderName = "X-XSRF-TOKEN";

        if (!hasCsrfToken()) await axios.get("/api/csrf", {withCredentials: true});
        attachCsrfInterceptor();
    } else if (config === 'jwt') {
        attachJwtInterceptor();
    } else if (config === 'oauth') {
    } else {
        console.error('init broken!!')
    }

    return config;
}

const attachCsrfInterceptor = () => {
    client.interceptors.request.use(async (config) => {
        if (!hasCsrfToken()) {
            await axios.get("/api/csrf", {withCredentials: true});
        }
        return config;
    });
};

const attachJwtInterceptor = () => {
    client.interceptors.response.use(
        (response) => response,
        (error: AxiosError) => {
            if (error.response?.status === 401 && jwtToken) {
                clearJwtToken();
                window.dispatchEvent(new CustomEvent('logout'));
            }
            throw error;
        }
    );
};