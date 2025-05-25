import axios, {type AxiosError, type AxiosRequestConfig} from "axios";
import type {APIResponse, AuthType, CSRFToken, UserData} from "../types/APIResponse.tsx";
import type {UserDTO} from "../types/UserDTO.tsx";

export const client = axios.create({
    baseURL: "/api",
    withCredentials: true,
    headers: {
        "Content-Type": 'application/json'
    }
});

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

export async function getCSRF(): Promise<CSRFToken> {
    return requestWithData<CSRFToken>({
        method: "GET",
        url: "/csrf"
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

export async function validateJWT(): Promise<string> {
    return requestWithoutData({
        method: "POST",
        url: "/jwt"
    })
}

export async function login(user: UserDTO): Promise<UserData> {
    return requestWithData({
        method: "POST",
        url: "/login",
        data: user
    })
}

export async function logout(): Promise<string> {
    return requestWithoutData({
        method: "POST",
        url: "/logout"
    })
}

export async function init() {
    const config = await getConfig();

    if (config === 'session')
        setupSessionAuth();

    else if (config === 'jwt')
        console.log('jwt')

    else if (config === 'oauth')
        console.log('oauth')

    else
        console.error('init broken!!')

    return config;
}

const setupSessionAuth = () => {
    client.defaults.xsrfCookieName = "XSRF-TOKEN";
    client.defaults.xsrfHeaderName = "X-XSRF-TOKEN";

    client.interceptors.request.use(async (config) => {
        if (!document.cookie.includes("XSRF-TOKEN")) {
            await axios.get("/api/csrf", {withCredentials: true});
        }
        return config;
    });
};