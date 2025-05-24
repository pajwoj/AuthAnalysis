import toast from 'react-hot-toast';
import axios from "axios";
import type {APIResponse} from "../types/APIResponse.tsx";

type ToastType = 'success' | 'error';

export const showToast = (message: string, type: ToastType = 'success') => {
    switch (type) {
        case 'success':
            toast.success(message);
            break;
        case 'error':
            toast.error(message);
            break;
        default:
            toast(message);
    }
};

export const handleAPIError = (error: unknown): string => {
    if (axios.isAxiosError<APIResponse>(error)) {
        const message = error.response?.data.message ?? 'Request failed';
        showToast(message, 'error');
        return message;
    }
    if (error instanceof Error) {
        showToast(error.message, 'error');
        return error.message;
    }
    const fallback = 'Unknown error occurred';
    showToast(fallback, 'error');
    return fallback;
};