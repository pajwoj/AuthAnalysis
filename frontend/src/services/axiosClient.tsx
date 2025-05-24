import axios from "axios";
import type {AuthType} from "../types/AuthType.tsx";

const client = axios.create({
    baseURL: "/api",
    withCredentials: true,
    headers: {
        "Content-Type": 'application/json'
    }
});

let authType: AuthType = null;
let authTypePromise: Promise<AuthType> | null = null;

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

const resolveAuthType = async (): Promise<AuthType> => {
    if (authType !== null) return authType;

    authTypePromise ??= axios
        .get("/api/config", {withCredentials: true})
        .then((r) => {
            authType = r.data as AuthType;

            if (authType === "session") setupSessionAuth();
            else if (authType === "oauth" || authType === 'jwt') console.log(authType);
            else console.error("Unknown auth type:", authType);

            return authType;
        })
        .catch((e: unknown) => {
            console.error("Failed to fetch auth type:", e);
            authType = null;
            return authType;
        });

    return await authTypePromise;
};

client.interceptors.request.use(async (config) => {
    await resolveAuthType();
    return config;
});

export default client;
export const getAuthType = resolveAuthType;
