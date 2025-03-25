import { ChangeEvent, useState } from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";

function HomePage() {
    const [code, setCode] = useState<string>("");
    const navigate = useNavigate();

    const handleCodeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCode(e.target.value.toUpperCase());
    };

    return (
        <div className="relative min-h-screen overflow-hidden">
            {/* GIF jako tło */}
            <div
                className="absolute inset-0 bg-cover bg-center z-[-1]"
                style={{backgroundImage: "url('/BluffWord_Logo.gif')"}} // wrzuć do public/
            />

            {/* Warstwa przyciemnienia / blur */}
            <div className="absolute inset-0 bg-black/50 backdrop-blur-sm z-[-1]"/>

            {/* Twoja oryginalna treść */}
            <div className="min-h-screen flex items-center justify-center px-4">
                <div className="flex flex-col items-center w-full max-w-sm">
                    <h1 className="text-4xl font-bold text-white mb-10 flex items-center gap-2">
                        <span role="img" aria-label="controller">🎮</span> BluffWord
                    </h1>

                    <button
                        onClick={() => navigate("/enter-name?mode=CREATE")}
                        className="w-full bg-blue-600 text-white font-semibold px-6 py-3 rounded-xl shadow hover:bg-blue-700 transition cursor-pointer"
                    >
                        Create new room
                    </button>

                    <div className="mt-6 flex w-full gap-2">
                        <input
                            type="text"
                            placeholder="Enter room code"
                            value={code}
                            onChange={handleCodeChange}
                            className="flex-1 border border-gray-700 rounded px-4 py-2 text-sm bg-gray-800 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />

                        <button
                            onClick={( () => {
                                if (!code) return alert("Enter room code first!");
                                navigate(`/enter-name?code=${code}`);
                            } )}
                            className="bg-green-600 text-white font-semibold px-4 py-2 rounded-xl hover:bg-green-700 transition cursor-pointer"
                        >
                            Join
                        </button>
                    </div>
                </div>
            </div>
        </div>

    );


}

export default HomePage;
