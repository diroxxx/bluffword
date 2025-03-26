import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
// import Stomp from 'stompjs'

import axios from "axios";
import {useStompClient, useSubscription} from "react-stomp-hooks";

type PlayerInfo = {
    nickname: string;
};

function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [connected, setConnected] = useState(false);

    const stompClient = useStompClient();

    useSubscription(`/topic/room/${code}/players`, (message) => {
        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);
        console.log("Players update:", data);
    });

    useEffect(() => {
        const nickname = localStorage.getItem("nickname");
        if (!code || !nickname || !stompClient) return;

        axios
            .post(`http://localhost:8080/api/gameRoom/${code}/join`, { nickname })
            .then(() => console.log("Joined room"))
            .catch((err) => {
                console.error("Failed to join room", err);
                alert("Could not join the room.");
            });
    }, [code, stompClient]);

    return (
        <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center justify-center px-4">
            <h1 className="text-3xl font-bold mb-4">Room Code: {code}</h1>
            <h2 className="text-xl mb-6">
                {connected ? "Connected. Waiting for players..." : "🕓 Connecting..."}
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
                {/*<div> The broadcast message from websocket broker is {player}</div>*/}


            </div>
        </div>
    );
}

export default RoomLobby;
