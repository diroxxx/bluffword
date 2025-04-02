import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import { usePlayer } from "../PlayerContext";
import { useStompClient, useSubscription } from "react-stomp-hooks";


function EnterNicknamePage() {
    const [nickname, setNickname] = useState("");
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { setPlayer } = usePlayer();
    const stompClient = useStompClient();
    const mode = searchParams.get("mode");
    const existingCode = searchParams.get("code");


    // useSubscription("/user/queue/room/created", (message) => {
    //     const data = JSON.parse(message.body);
    //     const code = data.code;
    //
    //     setPlayer({
    //         nickname: nickname.trim(),
    //         isHost: true,
    //         isImpostor: false,
    //     });
    //     navigate(`/room/${code}`);
    // });


    // useSubscription("/user/queue/room/joined", (message) => {
    //     const data = JSON.parse(message.body);
    //     const code = data.code;
    //     const players = data.players;
    //
    //     setPlayer({
    //         nickname: nickname.trim(),
    //         isHost: false,
    //         isImpostor: false,
    //     });
    //
    //     setPlayers(players); // 🔥 teraz dostępne globalnie
    //
    //     navigate(`/room/${code}`);
    // });


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
                setPlayer({
                    nickname: nickname.trim(),
                    isImpostor: false,
                    isHost: true,
                });

                // stompClient.publish({
                //     destination: "/app/room/create",
                //     body: JSON.stringify({
                //         nickname: nickname.trim(),
                //         isImpostor: false, // lub null, zależnie jak w backendzie
                //     }),
                // });
                //


            } else if (mode === "JOIN") {
                await axios.post(`http://localhost:8080/api/gameRoom/${roomCode}/join`, {
                    nickname: nickname.trim(),
                    isImpostor: null,
                    isHost: false,
                });
                setPlayer({
                    nickname: nickname.trim(),
                    isHost: false,
                    isImpostor: false,
                });

                // stompClient.publish({
                //     destination: `/app/room/${existingCode}/join`,
                //     body: JSON.stringify({
                //         nickname: nickname.trim(),
                //         isImpostor: false,
                //         isHost: false,
                //     }),
                // });

                // navigate(`/room/${roomCode}`);
            }

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
