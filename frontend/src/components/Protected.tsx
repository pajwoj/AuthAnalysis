import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import client from '../axios/axiosClient';
import axios from 'axios';
import type {ErrorResponse} from '../interfaces/ErrorResponse';
import type {UserResponse} from "../interfaces/UserResponse.tsx";

export default function Protected() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<ErrorResponse | null>(null);
    const [data, setData] = useState<UserResponse | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const checkAccess = async () => {
            try {
                const response = await client.get<UserResponse>('/protected');
                setData(response.data);
                setLoading(false);
            } catch (error: unknown) {
                if (axios.isAxiosError<ErrorResponse>(error)) {
                    if (error.response?.status === 403) {
                        // Forbidden - user is authenticated but not an admin
                        const state = {
                            toast: {
                                message: 'You do not have admin permissions to access this page',
                                type: 'error' as const,
                            }
                        };
                        await navigate('/', {state});
                        return;
                    } else if (error.response?.status === 401) {
                        // Unauthorized - user is not logged in
                        const state = {
                            toast: {
                                message: 'You need to log in to access this page',
                                type: 'error' as const,
                            }
                        };
                        await navigate('/login', {state});
                        return;
                    } else {
                        setError(error.response?.data ?? {
                            timestamp: new Date().toISOString(),
                            error: 'ERROR',
                            message: 'An unexpected error occurred'
                        });
                        setLoading(false);
                    }
                } else {
                    setError({
                        timestamp: new Date().toISOString(),
                        error: 'ERROR',
                        message: error instanceof Error ? error.message : 'Unknown error'
                    });
                    setLoading(false);
                }
            }
        };

        void checkAccess();
    }, [navigate]);

    if (loading) {
        return <div>Checking access permissions...</div>;
    }

    if (error) {
        return (
            <div className="bg-red-50 p-4 rounded-md border border-red-200">
                <h1 className="text-xl font-bold text-red-800 mb-2">Access Denied</h1>
                <p className="text-red-600">{error.message}</p>
                <p className="text-sm text-red-500 mt-4">
                    Only administrators can access this protected area.
                </p>
                <button
                    type={"button"}
                    onClick={() => void navigate('/')}
                    className="mt-4 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                >
                    Back to Home
                </button>
            </div>
        );
    }

    return (
        <div className="bg-green-50 p-6 rounded-md border border-green-200">
            <h1 className="text-xl font-bold text-green-800 mb-2">Protected Admin Area</h1>
            {data && (
                <div className="text-green-700">
                    <p className="mb-2">Welcome, {data.email}!</p>
                    <p className="mb-4">Role: {data.roles}</p>
                    <p className="text-xs text-green-600">
                        Access granted at: {new Date(data.timestamp).toLocaleString()}
                    </p>
                </div>
            )}
            <button
                type={"button"}
                onClick={() => void navigate('/')}
                className="mt-4 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            >
                Back to Home
            </button>
        </div>
    );
}