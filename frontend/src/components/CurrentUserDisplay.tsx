import {useEffect, useState} from 'react';
import type {UserData} from "../types/APIResponse.tsx";
import {getUser, logout} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function CurrentUserDisplay() {
    const [user, setUser] = useState<UserData | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        void (async () => {
            try {
                setUser(await getUser());
            } catch (e) {
                const error = e instanceof Error ? e.message : "Unknown error";
                showToast(error, 'error');
            } finally {
                setLoading(false)
            }
        })();

        const handleLogout = () => {
            void logoutFunction();
        };

        window.addEventListener('logout', handleLogout);
        return () => {
            window.removeEventListener('logout', handleLogout);
        };
    }, []);

    const logoutFunction = async () => {
        try {
            const message = await logout();
            setUser(null);
            showToast(message);
        } catch (e) {
            const error = e instanceof Error ? e.message : "Unknown error";
            showToast(error, 'error');
        }
    };

    if (loading) {
        return (
            <div className="card mt-3">
                <div className="card-body text-center">
                    <div className="spinner-border spinner-border-sm text-secondary me-2" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <small className="text-muted">Checking auth...</small>
                </div>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="card mt-3 border-warning">
                <div className="card-body text-center">
                    <i className="fas fa-user-slash text-warning mb-2"></i>
                    <p className="card-text text-muted mb-0">
                        <small>No active auth</small>
                    </p>
                </div>
            </div>
        );
    }

    return (
        <div className="card mt-3 border-info">
            <div className="card-header bg-info text-white py-2">
                <h6 className="card-title mb-0">
                    <i className="fas fa-user-circle me-2"></i>
                    Current Auth
                </h6>
            </div>
            <div className="card-body py-3">
                <div className="row g-2">
                    <div className="col-12">
                        <div className="d-flex align-items-center">
                            <i className="fas fa-envelope text-muted me-2"></i>
                            <span className="fw-bold me-2">Email:</span>
                            <span className="text-break">{user.email}</span>
                        </div>
                    </div>
                    <div className="col-12">
                        <div className="d-flex align-items-start">
                            <i className="fas fa-user-tag text-muted me-2 mt-1"></i>
                            <span className="fw-bold me-2">Roles:</span>
                            <div>
                                {user.roles.map((role) => (
                                    <span key={role} className="badge bg-secondary me-1 mb-1">
                                        {role}
                                    </span>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
                <hr className="my-2"/>
                <div className="text-center">
                    <small className="text-muted">
                        <i className="fas fa-clock me-1"></i>
                        Auth active: {new Date().toLocaleString()}
                    </small>
                </div>
            </div>
        </div>
    );
}