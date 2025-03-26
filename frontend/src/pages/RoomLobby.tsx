import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import { useStompClient, useSubscription } from "react-stomp-hooks";

type PlayerInfo = {
    nickname: string;
};

function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [connected, setConnected] = useState(false);
    const [searchParams] = useSearchParams();

    const stompClient = useStompClient();

    // 🔔 Subskrypcja WebSocket — reaguj na zmiany listy graczy
    useSubscription(`/topic/room/${code}/players`, (message) => {
        console.log("🔔 WS received!", message.body); // <== DODAJ TO

        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);

    });

    useEffect(() => {
        // const mode = searchParams.get("mode");
        if (!stompClient || !code ) return;

        console.log("📡 Sending sync request to server...");
        stompClient.publish({
            destination: `/app/room/${code}/sync`,
            body: ""
        });

        setConnected(true);
    }, [stompClient, code, searchParams]);

    useEffect(() => {
        const nickname = localStorage.getItem("nickname");

        const handleBeforeUnload = () => {
            if (!code || !nickname) return;
            navigator.sendBeacon(
                `http://localhost:8080/api/gameRoom/${code}/leave`,
                new Blob([JSON.stringify({ nickname })], { type: "application/json" })
            );
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
        };
    }, [code]);



    return (
        <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center px-4">
            <h1 className="text-3xl font-bold mb-4">Room Code: {code}</h1>
            <h2 className="text-xl mb-6">
                {connected ? "🟢 Connected. Waiting for players..." : "🕓 Connecting..."}
            </h2>

            <div className="bg-gray-800 p-4 rounded-lg shadow w-full max-w-sm">
                <h3 className="text-lg font-semibold mb-2">Players in lobby:</h3>
                <ul className="list-disc pl-5 space-y-1">
                    {players.length === 0 ? (
                        <li>No players yet</li>
                    ) : (
                        players.map((p, i) => <li key={i}>{p.nickname}</li>)
                    )}
                </ul>
            </div>
        </div>
    );
}

export default RoomLobby;
