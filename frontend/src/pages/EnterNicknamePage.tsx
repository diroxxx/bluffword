import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import { usePlayer } from "../PlayerContext";
import { useStompClient, useSubscription } from "react-stomp-hooks";


export type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

function EnterNicknamePage() {
    const [nickname, setNickname] = useState("");
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    // const { setPlayer } = usePlayer();
    const stompClient = useStompClient();
    const mode = searchParams.get("mode");
    const existingCode = searchParams.get("code");



    const handleSubmit = async () => {
        if (!nickname.trim()) {
            alert("Please enter a nickname");
            return;
        }
        if (!stompClient) {
            alert("Not connected to server.");
            return;
        }

        try {
            let roomCode = existingCode;
            const currentPlayer: PlayerInfo = {
                nickname: "",
                isHost: false,
                isImpostor: false
            };
            if (mode === "CREATE") {
                const res = await axios.post<{ code: string }>(
                    "http://localhost:8080/api/gameRoom/create",
                    {
                        nickname: nickname.trim(),
                        isImpostor: null,
                        isHost: true,
                    }
                );
                roomCode = res.data.code;
                currentPlayer.nickname = nickname
                currentPlayer.isHost = true;
                currentPlayer.isImpostor = false;
                // setPlayer({
                //     nickname: nickname.trim(),
                //     isImpostor: false,
                //     isHost: true,
                // });

            } else if (mode === "JOIN") {
                await axios.post(`http://localhost:8080/api/gameRoom/${roomCode}/join`, {
                    nickname: nickname.trim(),
                    isImpostor: null,
                    isHost: false,
                });
                // setPlayer({
                //     nickname: nickname.trim(),
                //     isHost: false,
                //     isImpostor: false,
                // });

                currentPlayer.nickname = nickname
                currentPlayer.isHost = false;
                currentPlayer.isImpostor = false;
            }
            sessionStorage.setItem("currentPlayer", JSON.stringify(currentPlayer));
            navigate(`/room/${roomCode}?mode=${mode}`);
        } catch (err: any) {
            if (err.response?.status === 409) {
                alert("This nickname is already taken in this room.");
            } else {
                alert("Something went wrong.");
            }
            console.error(err);
        }
    };

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-900 text-white px-4">
            <h1 className="text-3xl font-bold mb-4">Enter your nickname</h1>
            <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                className="px-4 py-2 rounded bg-gray-800 text-white w-full max-w-xs mb-4"
                placeholder="Your name..."
            />
            <button
                onClick={handleSubmit}
                className="bg-blue-600 px-6 py-2 rounded hover:bg-blue-700 transition"
            >
                Continue
            </button>
        </div>
    );
}

export default EnterNicknamePage;
