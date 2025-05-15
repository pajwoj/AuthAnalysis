import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Toaster} from "react-hot-toast";
import Login from "./components/Login";
import Home from "./components/Home";
import Protected from "./components/Protected";
import Logout from "./components/Logout.tsx";

export default function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/protected" element={<Protected/>}/>
                    <Route path="/logout" element={<Logout/>}/>
                </Routes>
            </BrowserRouter>
            <Toaster position="top-center"/>
        </>
    );
}