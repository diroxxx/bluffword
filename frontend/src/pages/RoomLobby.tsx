import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import axios from "axios";

type PlayerInfo = {
    nickname: string;
};

function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [stompClient, setStompClient] = useState<Client | null>(null);

    useEffect(() => {
        if (!code) return;

        const nickname = localStorage.getItem("nickname");
        if (!nickname) {
            alert("Nickname not found!");
            return;
        }

        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("✅ Connected to WebSocket");

                // 🔔 Subskrypcja listy graczy
                client.subscribe(`/topic/room/${code}/players`, (message: IMessage) => {
                    console.log("📨 WS message:", message.body);
                    const data = JSON.parse(message.body) as PlayerInfo[];
                    setPlayers(data);
                });

                // ✅ Dołączenie do pokoju
                axios
                    .post(`http://localhost:8080/api/gameRoom/${code}/join`, {
                        nickname,
                    })
                    .then(() => {
                        console.log("✅ Joined room successfully");
                    })
                    .catch((err) => {
                        console.error("❌ Failed to join room:", err);
                        alert("Failed to join the room.");
                    });
            },
            onStompError: (error) => {
                console.error("🛑 STOMP error:", error);
            },
        });

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, [code]);

    return (
        <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center px-4">
            <h1 className="text-3xl font-bold mb-4">Room Code: {code}</h1>
            <h2 className="text-xl mb-6">🕓 Waiting for players to join...</h2>

            <div className="bg-gray-800 p-4 rounded-lg shadow w-full max-w-sm">
                <h3 className="text-lg font-semibold mb-2">Players in lobby:</h3>
                <ul className="list-disc pl-5 space-y-1">
                    {players.length === 0
                        ? <li>No players yet</li>
                        : players.map((p, i) => (
                            <li key={i}>{p.nickname}</li>
                        ))}
                </ul>
            </div>
        </div>
    );
}

export default RoomLobby;
