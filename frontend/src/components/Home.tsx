import {Link, useLocation} from 'react-router-dom';
import CurrentUserDisplay from "./CurrentUserDisplay.tsx";
import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import type {AuthType} from "../types/AuthType.tsx";
import {getAuthType} from "../axios/axiosClient.tsx";

export default function Home() {
    const location = useLocation();
    const [authType, setAuthType] = useState<AuthType>(null);

    useEffect(() => {
        const fetchAuthType = async () => {
            const type = await getAuthType();
            setAuthType(type);
        };

        void fetchAuthType();

        const state = location.state as { toast?: { message: string, type: 'success' | 'error' } } | undefined;

        if (state?.toast) {
            const {message, type} = state.toast;

            if (type === 'success') {
                toast.success(message);
            } else {
                toast.error(message);
            }

            const newState = {...state};
            delete newState.toast;

            window.history.replaceState(newState, '');
        }
    }, [location.state]);

    if (authType === 'oauth') {
        return (
            <div>
                <h1>Welcome to the Main Page</h1>
                <p>This page is public. No login required.</p>

                <Link to="/login">
                    <button type={"button"}>
                        LOGIN
                    </button>
                </Link>

                <button
                    type="button"
                    onClick={() => {
                        window.location.href = "http://localhost:8080/oauth2/authorization/google";
                    }}
                >
                    LOGIN WITH GOOGLE
                </button>

                <button
                    type="button"
                    onClick={() => {
                        window.location.href = "http://localhost:8080/oauth2/authorization/github";
                    }}
                >
                    LOGIN WITH GITHUB
                </button>

                <button
                    type="button"
                    onClick={() => {
                        window.location.href = "http://localhost:8080/oauth2/authorization/gitlab";
                    }}
                >
                    LOGIN WITH GITLAB
                </button>

                <button
                    type="button"
                    onClick={() => {
                        window.location.href = "http://localhost:8080/oauth2/authorization/discord";
                    }}
                >
                    LOGIN WITH DISCORD
                </button>

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