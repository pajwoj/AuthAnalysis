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
        <div>
            <h1>Login</h1>

            <form onSubmit={onSubmit}>
                <div>
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        type="text"
                        value={email}
                        onChange={(e) => {
                            setEmail(e.target.value);
                        }}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => {
                            setPassword(e.target.value);
                        }}
                        required
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Logging in..." : "Login"}
                </button>
            </form>
        </div>
    );
}