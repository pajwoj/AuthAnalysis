import {type FormEvent, useState} from "react";
import {useNavigate} from "react-router-dom";
import type {UserDTO} from "../types/UserDTO.tsx";
import {login} from "../services/axiosClient.tsx";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
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
            await navigate("/");
        } catch (e) {
            setError(e instanceof Error ? e.message : "Unknown error")
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
            <div>{error}</div>

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