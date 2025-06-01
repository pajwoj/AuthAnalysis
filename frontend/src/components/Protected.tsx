import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import type {UserData} from '../types/APIResponse.tsx';
import {getProtected} from "../services/axiosClient.tsx";
import {showToast} from "../services/toastService.tsx";

export default function Protected() {
    const [loading, setLoading] = useState<boolean>(true);
    const [user, setUser] = useState<UserData | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        void (async () => {
            try {
                const response: UserData = await getProtected();
                setUser(response);
                showToast('Hello, ' + response.email + '!')
            } catch (e) {
                const error = e instanceof Error ? e.message : "Unknown error";
                showToast(error, 'error');
                await navigate('/', {replace: true})
            } finally {
                setLoading(false);
            }
        })();
    }, [navigate]);

    if (loading) {
        return (
            <div className="container mt-5">
                <div className="row justify-content-center">
                    <div className="col-md-6">
                        <div className="card shadow">
                            <div className="card-body text-center">
                                <div className="spinner-border text-primary mb-3" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                                <p className="text-muted">Checking access permissions...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="card shadow border-success">
                        <div className="card-header bg-success text-white">
                            <h1 className="card-title mb-0">
                                <i className="fas fa-shield-alt me-2"></i>
                                Protected Admin Area
                            </h1>
                        </div>
                        <div className="card-body bg-light">
                            {user && (
                                <div>
                                    <div className="alert alert-success" role="alert">
                                        <h5 className="alert-heading">
                                            <i className="fas fa-user-check me-2"></i>
                                            Access Granted!
                                        </h5>
                                        <p className="mb-2">Welcome, <strong>{user.email}</strong>!</p>
                                        <hr/>
                                        <p className="mb-0">
                                            <span className="badge bg-primary me-2">Role:</span>
                                            {user.roles.map((role) => (
                                                <span key={role} className="badge bg-secondary me-1">
                                                    {role}
                                                </span>
                                            ))}
                                        </p>
                                    </div>

                                    <div className="card">
                                        <div className="card-body">
                                            <small className="text-muted">
                                                <i className="fas fa-clock me-1"></i>
                                                Access granted at: {new Date().toLocaleString()}
                                            </small>
                                        </div>
                                    </div>
                                </div>
                            )}

                            <div className="text-center mt-4">
                                <button
                                    type="button"
                                    onClick={() => void navigate('/')}
                                    className="btn btn-outline-success btn-lg"
                                >
                                    <i className="fas fa-home me-2"></i>
                                    Back to Home
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}