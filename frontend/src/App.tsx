import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Toaster} from "react-hot-toast";
import Login from "./components/Login";
import Home from "./components/Home";
import Protected from "./components/Protected";
import {useEffect, useState} from "react";
import {init} from "./services/axiosClient.tsx";
import type {AuthType} from "./types/APIResponse.tsx";
import {AuthContext} from "./services/authContextService.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';

export default function App() {
    const [auth, setAuth] = useState<AuthType | null>(null);

    useEffect(() => {
        void (async () => {
            try {
                setAuth(await init())
            } catch (e) {
                console.error(e)
            }
        })();

    }, []);

    return (
        <>
            <AuthContext value={auth}>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<Home/>}/>
                        <Route path="/login" element={<Login/>}/>
                        <Route path="/protected" element={<Protected/>}/>
                    </Routes>
                </BrowserRouter>
                <Toaster position="top-right"/>
            </AuthContext>
        </>
    );
}