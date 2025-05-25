import {createContext} from "react";
import type {AuthType} from "../types/APIResponse.tsx";

export const AuthContext = createContext<AuthType>(null);