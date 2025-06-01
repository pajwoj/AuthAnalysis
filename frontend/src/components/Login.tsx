import {type FormEvent, useState} from "react";
import {useNavigate} from "react-router-dom";
import type {UserDTO} from "../types/UserDTO.tsx";
import {login} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);

        const user: UserDTO = {
            email: email,
            password: password
        }

        try {
            await login(user);
            showToast("Successfully logged in")
            await navigate("/");
        } catch (e) {
            const error = e instanceof Error ? e.message : "Unknown error";
            showToast(error, 'error');
            await navigate("/", {replace: true});
        } finally {
            setLoading(false)
        }
    }

    const onSubmit = (e: FormEvent<HTMLFormElement>) => {
        void handleSubmit(e);
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card shadow">
                        <div className="card-body">
                            <h1 className="card-title text-center mb-4">Login</h1>

                            <form onSubmit={onSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="email" className="form-label">
                                        Email
                                    </label>
                                    <input
                                        id="email"
                                        className="form-control"
                                        value={email}
                                        onChange={(e) => {
                                            setEmail(e.target.value);
                                        }}
                                        placeholder="Enter your email"
                                        required
                                    />
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">
                                        Password
                                    </label>
                                    <input
                                        id="password"
                                        type="password"
                                        className="form-control"
                                        value={password}
                                        onChange={(e) => {
                                            setPassword(e.target.value);
                                        }}
                                        placeholder="Enter your password"
                                        required
                                    />
                                </div>

                                <div className="d-grid">
                                    <button
                                        type="submit"
                                        className={`btn btn-primary btn-lg ${loading ? 'disabled' : ''}`}
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status"
                                                      aria-hidden="true"></span>
                                                Logging in...
                                            </>
                                        ) : (
                                            "Login"
                                        )}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}