import {Link} from 'react-router-dom';
import CurrentUserDisplay from "./CurrentUserDisplay.tsx";
import {use} from "react";
import {AuthContext} from "../services/authContextService.tsx";

export default function Home() {
    const authType = use(AuthContext);

    if (authType === 'oauth') {
        return (
            <div>
                <h1>Welcome to the Main Page</h1>
                <p>This page is public. No login required.</p>

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

                <button
                    type="button"
                    onClick={() => window.dispatchEvent(new CustomEvent('logout'))}
                >
                    LOGOUT
                </button>

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

            <button
                type="button"
                onClick={() => window.dispatchEvent(new CustomEvent('logout'))}
            >
                LOGOUT
            </button>

            <CurrentUserDisplay/>
        </div>
    );
}