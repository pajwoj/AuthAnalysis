import {toast} from "react-hot-toast";
import type {FormEvent} from "react";

export default function Login() {
    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        toast.success("Login button clicked (no backend yet)");
    };

    return (
        <div>
            <h1>Login Page</h1>
            <form onSubmit={handleSubmit}>
                <input type="text" placeholder="Username"/>
                <input type="password" placeholder="Password"/>
                <button type="submit">Login</button>
            </form>
        </div>
    );
}