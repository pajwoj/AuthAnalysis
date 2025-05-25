import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import type {UserData} from '../types/APIResponse.tsx';
import {getProtected} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function Protected() {
    const [loading, setLoading] = useState<boolean>(true);
    const [user, setUser] = useState<UserData | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        void (async () => {
            try {
                const response: UserData = await getProtected();
                setUser(response);
                showToast('Hello, ' + response.email + '!')
            } catch (e) {
                const error = e instanceof Error ? e.message : "Unknown error";
                showToast(error, 'error');
                await navigate('/', {replace: true})
            } finally {
                setLoading(false);
            }
        })();
    }, [navigate]);

    if (loading) {
        return <div>Checking access permissions...</div>;
    }

    return (
        <div className="bg-green-50 p-6 rounded-md border border-green-200">
            <h1 className="text-xl font-bold text-green-800 mb-2">Protected Admin Area</h1>
            {user && (
                <div className="text-green-700">
                    <p className="mb-2">Welcome, {user.email}!</p>
                    <p className="mb-4">Role: {user.roles.join(', ')}</p>
                    <p className="text-xs text-green-600">
                        Access granted at: {new Date().toLocaleString()}
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