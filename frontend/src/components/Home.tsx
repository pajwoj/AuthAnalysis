import {Link} from 'react-router-dom';
import CurrentUserDisplay from "./CurrentUserDisplay.tsx";

export default function Home() {
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

            <CurrentUserDisplay/>
        </div>
    );
}