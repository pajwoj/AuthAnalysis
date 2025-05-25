export interface APIResponse<T = never> {
    timestamp: string;
    message: string;
    data?: T;
}

export interface UserData {
    email: string;
    roles: string[];
}

export interface CSRFToken {
    csrf: string;
}

export type UserResponse = APIResponse<UserData>;

export type AuthType = "jwt" | "session" | "oauth" | null;