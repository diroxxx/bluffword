import { ChangeEvent, useState } from "react";
import axios from "axios";

type GameRoomResponse = {
    id: number;
    code: string;
    gameMode: string;
    roundsTotal: number;
};

function HomePage() {
    const [code, setCode] = useState<string>("");

    const createRoom = async () => {
        try {
            const res = await axios.post<GameRoomResponse>(
                "http://localhost:8080/api/rooms",
                null,
                {
                    params: {
                        mode: "STATIC_IMPOSTOR",
                        rounds: 5,
                    },
                }
            );
            alert(`Kod pokoju: ${res.data.code}`);
        } catch (e) {
            console.error(e);
            alert("Błąd przy tworzeniu pokoju.");
        }
    };

    const joinRoom = () => {
        if (!code) return alert("Wpisz kod pokoju!");
        alert(`Dołączasz do pokoju: ${code}`);
    };

    const handleCodeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCode(e.target.value.toUpperCase());
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 px-4">
            <div className="flex flex-col items-center w-full max-w-sm">
                <h1 className="text-4xl font-bold text-white mb-10 flex items-center gap-2">
                    <span role="img" aria-label="controller">🎮</span> BluffWord
                </h1>

                <button
                    onClick={createRoom}
                    className="w-full bg-blue-600 text-white font-semibold px-6 py-3 rounded-xl shadow hover:bg-blue-700 transition"
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
                        onClick={joinRoom}
                        className="bg-green-600 text-white font-semibold px-4 py-2 rounded-xl hover:bg-green-700 transition"
                    >
                        Join
                    </button>
                </div>
            </div>
        </div>
    );


}

export default HomePage;
