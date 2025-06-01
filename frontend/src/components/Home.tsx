import {Link} from 'react-router-dom';
import CurrentUserDisplay from "./CurrentUserDisplay.tsx";
import {use} from "react";
import {AuthContext} from "../services/authContextService.tsx";

export default function Home() {
    const authType = use(AuthContext);

    if (authType === 'oauth') {
        return (
            <div className="container mt-5">
                <div className="row justify-content-center">
                    <div className="col-md-8 col-lg-6">
                        <div className="card shadow">
                            <div className="card-body text-center">
                                <h1 className="card-title mb-3">Welcome to the Main Page</h1>
                                <p className="card-text text-muted mb-4">This page is public. No login required.</p>

                                <div className="d-grid gap-2 mb-4">
                                    <button
                                        type="button"
                                        className="btn btn-danger btn-lg"
                                        onClick={() => {
                                            window.location.href = "http://localhost:8080/oauth2/authorization/google";
                                        }}
                                    >
                                        <i className="fab fa-google me-2"></i>
                                        LOGIN WITH GOOGLE
                                    </button>

                                    <button
                                        type="button"
                                        className="btn btn-dark btn-lg"
                                        onClick={() => {
                                            window.location.href = "http://localhost:8080/oauth2/authorization/github";
                                        }}
                                    >
                                        <i className="fab fa-github me-2"></i>
                                        LOGIN WITH GITHUB
                                    </button>

                                    <button
                                        type="button"
                                        className="btn btn-warning btn-lg"
                                        onClick={() => {
                                            window.location.href = "http://localhost:8080/oauth2/authorization/gitlab";
                                        }}
                                    >
                                        <i className="fab fa-gitlab me-2"></i>
                                        LOGIN WITH GITLAB
                                    </button>

                                    <button
                                        type="button"
                                        className="btn btn-primary btn-lg"
                                        onClick={() => {
                                            window.location.href = "http://localhost:8080/oauth2/authorization/discord";
                                        }}
                                    >
                                        <i className="fab fa-discord me-2"></i>
                                        LOGIN WITH DISCORD
                                    </button>
                                </div>

                                <div className="d-flex gap-2 justify-content-center mb-3">
                                    <Link to="/protected" className="btn btn-success">
                                        PROTECTED
                                    </Link>

                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        onClick={() => window.dispatchEvent(new CustomEvent('logout'))}
                                    >
                                        LOGOUT
                                    </button>
                                </div>

                                <CurrentUserDisplay/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="card shadow">
                        <div className="card-body text-center">
                            <h1 className="card-title mb-3">Welcome to the Main Page</h1>
                            <p className="card-text text-muted mb-4">This page is public. No login required.</p>

                            <div className="d-flex gap-3 justify-content-center mb-4">
                                <Link to="/login" className="btn btn-primary btn-lg">
                                    LOGIN
                                </Link>

                                <Link to="/protected" className="btn btn-success btn-lg">
                                    PROTECTED
                                </Link>
                            </div>

                            <button
                                type="button"
                                className="btn btn-outline-secondary mb-3"
                                onClick={() => window.dispatchEvent(new CustomEvent('logout'))}
                            >
                                LOGOUT
                            </button>

                            <CurrentUserDisplay/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}