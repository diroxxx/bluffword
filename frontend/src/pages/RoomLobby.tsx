import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { usePlayer } from "../context/PlayerContext";
import PlayerList from "../components/PlayerList";
import RoomCode from "../components/RoomCode";
import GameRoomSettings from "../components/GameRoomSettings";

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
        <div className="min-h-screen bg-gray-900 text-white flex justify-center px-4 py-6">
            <div className="w-full max-w-6xl">
                <div className="text-center mb-8">
                    <RoomCode connected={connected} code={code} />
                </div>

                <div className="flex flex-col lg:flex-row justify-center items-start gap-10">
                    <div className="w-full lg:w-1/2 flex justify-center">
                        <PlayerList players={players} />
                    </div>
                    <div className="w-full lg:w-1/2 flex justify-center">
                        {/*{player?.isHost && <GameRoomSettings />}*/}
                        <GameRoomSettings />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default RoomLobby;
