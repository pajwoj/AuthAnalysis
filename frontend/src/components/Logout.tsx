import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {logout} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function Logout() {
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        void (async () => {
            try {
                await logout();
                await navigate("/");
            } catch (e) {
                setError(e instanceof Error ? e.message : "Unknown error")
            }
        })();
    }, [navigate]);

    if (error) {
        showToast(error, 'error');
    }

    return <div>Logging out...</div>;
}