import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { usePlayer } from "../PlayerContext";
import PlayerList from "../pages/playerGameRoomComponents/PlayerList"
import RoomCode from "../pages/playerGameRoomComponents/RoomCode";
import GameRoomSettings from "../pages/hostGameRoomComponents/GameRoomSettings";

type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

type GameRoomSettings = {
    mode: string;
    numberOfRounds: number;
    numbersOfPlayers: number;
};

function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [connected, setConnected] = useState(false);
    const [searchParams] = useSearchParams();
    const stompClient = useStompClient();
    const { player } = usePlayer();

    // if (loading) return null; // lub spinner


    useEffect(() => {
        if (!stompClient || !code) return;

        stompClient.publish({
            destination: `/app/room/${code}/sync`,
            body: ""
        });

        console.log("📡 Sent sync request after refresh");
    }, [stompClient, code]);


    useSubscription(`/topic/room/${code}/players`, (message) => {
        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);
    });

    useEffect(() => {
        if (stompClient && code) {
            setConnected(true);
        }
    }, [stompClient, code]);

    return (
        <div className="min-h-screen bg-gray-900 text-white px-4 py-6 flex flex-col items-center">
            {/* Room Code */}
            <div className="text-center mb-8">
                <RoomCode connected={connected} code={code}/>
            </div>

            {/* Ustawienia i lista graczy obok siebie */}
            <div className="flex flex-row justify-center items-start gap-12 max-w-4xl w-full">
                {/* Game Settings (left) */}
                {player?.isHost && (
                    <div className="flex-shrink-0 w-80">
                        <GameRoomSettings/>
                    </div>
                )}


                {/* Player List (centered) */}
                <div className="flex-grow flex justify-center">
                    <PlayerList players={players}/>
                </div>
            </div>
        </div>


    );
}

export default RoomLobby;
