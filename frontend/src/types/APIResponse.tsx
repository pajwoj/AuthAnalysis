export interface APIResponse<T = never> {
    timestamp: string;
    message: string;
    data?: T;
}

export interface UserData {
    email: string;
    roles: string[];
}

export type UserResponse = APIResponse<UserData>;