import {useEffect, useState} from 'react';
import axios, {type AxiosResponse} from 'axios';
import type {ErrorResponse} from "../interfaces/ErrorResponse.tsx";
import type {UserResponse} from "../interfaces/UserResponse.tsx";
import client from "../axios/axiosClient.tsx";

export default function CurrentUserDisplay() {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [error, setError] = useState<ErrorResponse | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        client.get("/user")
            .then((response: AxiosResponse<UserResponse>) => {
                setUser(response.data);
            })

            .catch((error: unknown) => {
                if (axios.isAxiosError<ErrorResponse>(error)) {
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
        return <div className="text-gray-500">Checking session...</div>;
    }

    if (error) {
        return (
            <div className="bg-red-50 p-4 rounded-md border border-red-200">
                <h3 className="font-medium text-red-800">Session Error</h3>
                <p className="text-red-600">{error.message}</p>
                <p className="text-xs text-red-500 mt-2">
                    {new Date(error.timestamp).toLocaleString()}
                </p>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="bg-yellow-50 p-4 rounded-md border border-yellow-200">
                <p className="text-yellow-800">No active session</p>
            </div>
        );
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