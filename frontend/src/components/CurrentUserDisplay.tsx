import {useEffect, useState} from 'react';
import axios from 'axios';
import type {APIResponse, UserData, UserResponse} from "../types/APIResponse.tsx";
import client from "../services/axiosClient.tsx";

export default function CurrentUserDisplay() {
    const [user, setUser] = useState<UserData | null>(null);
    const [timestamp, setTimestamp] = useState<string>("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await client.get<UserResponse>('/user');

                if (response.data.data) {
                    setUser(response.data.data);
                    setTimestamp(response.data.timestamp);
                    setError(null);
                } else {
                    throw new Error('No user data received');
                }
            } catch (error) {
                setError(getErrorMessage(error));
            } finally {
                setLoading(false);
            }
        };

        void fetchUser();
    }, []);

    const getErrorMessage = (error: unknown): string => {
        if (axios.isAxiosError<APIResponse>(error)) {
            return error.response?.data.message ?? 'Request failed';
        }
        if (error instanceof Error) {
            return error.message;
        }
        return 'Unknown error occurred';
    };

    if (loading) {
        return <div>Checking session...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    if (!user) {
        return <div>No active session</div>;
    }

    return (
        <div className="bg-white p-4 rounded-md shadow-sm border">
            <h3 className="font-medium mb-2">Current Session</h3>
            <div className="space-y-1 text-sm">
                <p>
                    <span className="font-medium">Email:</span> {user.email}
                </p>
                <p>
                    <span className="font-medium">Roles:</span> {user.roles.join(', ')}
                </p>
                <p className="text-xs text-gray-500 mt-2">
                    {new Date(timestamp).toLocaleString()}
                </p>
            </div>
        </div>
    );
}