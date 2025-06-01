export interface APIResponse<T = never> {
    timestamp: string;
    message: string;
    data?: T;
}

export interface UserData {
    email: string;
    roles: string[];
    jwt?: string;
}

export type AuthType = "jwt" | "session" | "oauth" | null;