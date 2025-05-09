import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Toaster} from "react-hot-toast";
import Login from "./components/Login";
import Home from "./components/Home";
import Protected from "./components/Protected";

export default function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/protected" element={<Protected/>}/>
                </Routes>
            </BrowserRouter>
            <Toaster position="bottom-right"/>
        </>
    );
}