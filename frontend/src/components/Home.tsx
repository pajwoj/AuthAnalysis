import {Link, useLocation} from 'react-router-dom';
import CurrentUserDisplay from "./CurrentUserDisplay.tsx";
import {useEffect} from "react";
import toast from "react-hot-toast";

export default function Home() {
    const location = useLocation();

    useEffect(() => {
        // Safely access the state with proper null checks
        const state = location.state as { toast?: { message: string, type: 'success' | 'error' } } | undefined;

        if (state?.toast) {
            const {message, type} = state.toast;

            // Show the toast
            if (type === 'success') {
                toast.success(message);
            } else {
                toast.error(message);
            }

            // Create a new state object without the toast, preserving other state
            const newState = {...state};
            delete newState.toast;

            // Update the history state without triggering navigation
            window.history.replaceState(newState, '');
        }
    }, [location.state]);

    return (
        <div>
            <h1>Welcome to the Main Page</h1>
            <p>This page is public. No login required.</p>

            <Link to="/login">
                <button type={"button"}>
                    LOGIN
                </button>
            </Link>

            <Link to="/protected">
                <button type={"button"}>
                    PROTECTED
                </button>
            </Link>

            <Link to="/logout">
                <button type={"button"}>
                    LOGOUT
                </button>
            </Link>

            <CurrentUserDisplay/>
        </div>
    );
}