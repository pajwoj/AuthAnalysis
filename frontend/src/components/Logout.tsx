import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {logout} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function Logout() {
    const navigate = useNavigate();

    useEffect(() => {
        void (async () => {
            try {
                const message = await logout();
                showToast(message);
                await navigate("/");
            } catch (e) {
                const error = e instanceof Error ? e.message : "Unknown error";
                showToast(error, 'error');
                await navigate("/", {replace: true});
            }
        })();
    }, [navigate]);
    
    return <div>Logging out...</div>;
}