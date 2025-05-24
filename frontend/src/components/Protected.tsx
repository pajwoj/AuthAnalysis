import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import client from '../services/axiosClient.tsx';
import axios from 'axios';
import type {APIResponse, UserResponse} from '../types/APIResponse.tsx';
import {handleAPIError, showToast} from '../services/toastService.tsx';

export default function Protected() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<APIResponse | null>(null);
    const [data, setData] = useState<UserResponse | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const checkAccess = async () => {
            try {
                const response = await client.get<UserResponse>('/protected');
                setData(response.data);
                setLoading(false);
                // Show success toast if there's a message
                if (response.data.message) {
                    showToast(response.data.message, 'success');
                }
            } catch (error: unknown) {
                if (axios.isAxiosError<APIResponse>(error)) {
                    if (error.response?.status === 403) {
                        // Forbidden - user is authenticated but not an admin
                        handleAPIError(error);
                        await navigate('/');
                        return;
                    } else if (error.response?.status === 401) {
                        // Unauthorized - user is not logged in
                        handleAPIError(error);
                        await navigate('/');
                        return;
                    } else {
                        const errorData = error.response?.data ?? {
                            timestamp: new Date().toISOString(),
                            message: 'An unexpected error occurred'
                        };
                        setError(errorData);
                        handleAPIError(error);
                        setLoading(false);
                    }
                } else {
                    const fallbackError = {
                        timestamp: new Date().toISOString(),
                        message: error instanceof Error ? error.message : 'Unknown error'
                    };
                    setError(fallbackError);
                    handleAPIError(error);
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
                    <p className="mb-2">Welcome, {data.data?.email}!</p>
                    <p className="mb-4">Role: {data.data?.roles.join(', ')}</p>
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