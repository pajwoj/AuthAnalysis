import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import client from '../axios/axiosClient';
import axios from 'axios';
import type {APIResponse} from '../types/APIResponse.tsx';

interface LogoutSuccessResponse {
    message?: string;
}

export default function Logout() {
    const navigate = useNavigate();

    useEffect(() => {
        const performLogout = async () => {
            try {
                const response = await client.post<LogoutSuccessResponse>('/logout');
                const state = {
                    toast: {
                        message: response.data.message ?? 'Successfully logged out',
                        type: 'success' as const,
                    }
                };
                await navigate('/', {state, replace: true});
            } catch (error: unknown) {
                let errorMessage = 'An unknown error occurred';

                if (axios.isAxiosError<APIResponse>(error)) {
                    if (error.response?.data.message) {
                        errorMessage = error.response.data.message;
                    } else {
                        errorMessage = error.message || 'Network error occurred';
                    }
                } else if (error instanceof Error) {
                    errorMessage = error.message;
                }

                const state = {
                    toast: {
                        message: errorMessage,
                        type: 'error' as const,
                    }
                };
                await navigate('/', {state, replace: true});
            }
        };

        void performLogout();
    }, [navigate]);

    return <div>Logging out...</div>;
}