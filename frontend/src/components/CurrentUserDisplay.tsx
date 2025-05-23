import {useEffect, useState} from 'react';
import axios, {type AxiosResponse} from 'axios';
import type {APIResponse} from "../types/APIResponse.tsx";
import type {UserResponse} from "../types/UserResponse.tsx";
import client from "../axios/axiosClient.tsx";

export default function CurrentUserDisplay() {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [error, setError] = useState<APIResponse | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        client.get("/user")
            .then((response: AxiosResponse<UserResponse>) => {
                setUser(response.data);
            })

            .catch((error: unknown) => {
                if (axios.isAxiosError<APIResponse>(error)) {
                    if (error.response) {
                        setError((error.response.data))
                    } else {
                        console.error('Axios error without response:', error.message);
                    }
                } else if (error instanceof Error) {
                    console.error('Native error:', error.message);
                } else {
                    console.error('Unknown error type:', error);
                }
            })

            .finally(() => {
                setLoading(false);
            })
    }, []);

    if (loading) {
        return <div>
            Checking session...
        </div>;
    }

    if (error) {
        return <div>
            {error.message}
        </div>;
    }

    if (!user) {
        return <div>
            No active session
        </div>;
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
                    {new Date(user.timestamp).toLocaleString()}
                </p>
            </div>
        </div>
    );
}