import toast from 'react-hot-toast';

type ToastType = 'success' | 'error';

export const showToast = (message: string, type: ToastType = 'success') => {
    switch (type) {
        case 'success':
            toast.success(message);
            break;
        case 'error':
            toast.error(message);
            break;
        default:
            toast(message);
    }
};