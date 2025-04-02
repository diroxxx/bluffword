import React, { useEffect, useState } from "react";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { usePlayer } from "../PlayerContext";
import PlayerList from "../pages/playerGameRoomComponents/PlayerList"
import RoomCode from "../pages/playerGameRoomComponents/RoomCode";
import GameRoomSettings from "../pages/hostGameRoomComponents/GameRoomSettings";
import axios from "axios";

type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

type GameRoomSettingss = {
    mode: string;
    numberOfRounds: number;
    maxNumbersOfPlayers: number;
};

function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    // const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [connected, setConnected] = useState(false);
    const [searchParams] = useSearchParams();
    const stompClient = useStompClient();
    const { player, setPlayer } = usePlayer();
    const [gameMode, setGameMode] = useState<GameRoomSettingss>({
        mode: "STATIC_IMPOSTOR",
        numberOfRounds: 6,
        maxNumbersOfPlayers: 6,
    })
    const {players,setPlayers} = usePlayer();
    const navigate = useNavigate();


    useEffect(() => {
        if (!stompClient || !code || !connected) return;

        const timeout = setTimeout(() => {
            stompClient.publish({
                destination: `/app/room/${code}/sync`,
                body: ""
            });
            console.log("📡 Sent sync request after connection");
        }, 300);

        return () => clearTimeout(timeout);
    }, [stompClient, code, connected]);


    useSubscription(`/topic/room/${code}/game`, (message) => {
        const body = message.body;

        if (body === "GAME_STARTED") {
            navigate(`/room/${code}/round`);
        }
    });


    useSubscription(`/topic/room/${code}/players`, (message) => {
        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);
    });

    useEffect(() => {
        if (stompClient && code) {
            const timeout = setTimeout(() => setConnected(true), 100);
            return () => clearTimeout(timeout);
        }
    }, [stompClient, code]);

    const handleStartGame = async () => {
        try {
            await axios.post(`http://localhost:8080/api/gameRoom/${code}/start`);
            alert("Game started!");
        } catch (err) {
            console.error("Failed to start game", err);
            alert("Error starting the game.");
        }
    };
    return (
        <div className="min-h-screen bg-gray-900 text-white px-4 py-6 flex flex-col items-center">
            <div className="text-center mb-8">
                <RoomCode code={code} numberOfPlayers={players.length} maxNumberOfPlayers={gameMode?.maxNumbersOfPlayers}/>
            </div>
            <div className="flex flex-row justify-center items-start gap-12 max-w-4xl w-full">
                {player?.isHost && (
                    <div className="flex-shrink-0 w-80">
                        <GameRoomSettings setGameMode={setGameMode}/>
                        <button
                            className="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded mt-4 w-full"
                            onClick={handleStartGame}
                        >
                            Start Game
                        </button>
                    </div>
                )}

                <div className="flex-grow flex justify-center">
                    <PlayerList players={players}/>
                </div>
            </div>

        </div>

    );
}

export default RoomLobby;
