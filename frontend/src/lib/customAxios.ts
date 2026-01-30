import axios, {type AxiosError, type InternalAxiosRequestConfig} from "axios";


const customAxios = axios.create({
    baseURL: "http://localhost:8080",
    headers: { "Content-Type": "application/json" },
    withCredentials: true, 
});