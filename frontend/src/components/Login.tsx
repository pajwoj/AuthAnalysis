import {type FormEvent, useState} from "react";
import {useNavigate} from "react-router-dom";
import type {UserDTO} from "../interfaces/UserDTO.tsx";
import client from "../axios/axiosClient.tsx";
import axios from "axios";
import type {APIResponse} from "../interfaces/APIResponse.tsx";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<APIResponse | null>(null);
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
            await client.post("/login", JSON.stringify(user));
            await navigate("/");
        } catch (error: unknown) {
            if (axios.isAxiosError<APIResponse>(error)) {
                if (error.response) {
                    setError(error.response.data);
                } else {
                    console.error("Axios error without response:", error.message);
                }
            } else if (error instanceof Error) {
                console.error("Native error:", error.message);
            } else {
                console.error("Unknown error type:", error);
            }
        } finally {
            setLoading(false);
        }

    }

    const onSubmit = (e: FormEvent<HTMLFormElement>) => {
        void handleSubmit(e);
    };

    return (
        <div>
            <h1>Login</h1>
            {error && <div>{error.message}</div>}

            <form onSubmit={onSubmit}>
                <div>
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        //type="email"
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