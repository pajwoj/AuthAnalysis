import {useEffect, useState} from 'react';
import type {UserData} from "../types/APIResponse.tsx";
import {getUser, logout} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function CurrentUserDisplay() {
    const [user, setUser] = useState<UserData | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        void (async () => {
            try {
                setUser(await getUser());
            } catch (e) {
                const error = e instanceof Error ? e.message : "Unknown error";
                showToast(error, 'error');
            } finally {
                setLoading(false)
            }
        })();

        const handleLogout = () => {
            void logoutFunction();
        };

        window.addEventListener('logout', handleLogout);
        return () => {
            window.removeEventListener('logout', handleLogout);
        };
    }, []);

    const logoutFunction = async () => {
        try {
            const message = await logout();
            setUser(null);
            showToast(message);
        } catch (e) {
            const error = e instanceof Error ? e.message : "Unknown error";
            showToast(error, 'error');
        }
    };

    if (loading) {
        return <div>Checking session...</div>;
    }

    if (!user) {
        return <div>No active session</div>;
    }

    return (
        <div className="bg-white p-4 rounded-md shadow-sm border">
            <h3 className="font-medium mb-2">Current user</h3>
            <div className="space-y-1 text-sm">
                <p>
                    <span className="font-medium">Email:</span> {user.email}
                </p>
                <p>
                    <span className="font-medium">Roles:</span> {user.roles.join(', ')}
                </p>
                <p className="text-xs text-gray-500 mt-2">
                    {new Date().toLocaleString()}
                </p>
            </div>
        </div>
    );
}