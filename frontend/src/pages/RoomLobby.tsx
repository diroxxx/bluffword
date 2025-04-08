import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import PlayerList from "../pages/playerGameRoomComponents/PlayerList"
import RoomCode from "../pages/playerGameRoomComponents/RoomCode";
import GameRoomSettings from "../pages/hostGameRoomComponents/GameRoomSettings";
import axios from "axios";
import {useAtom, useAtomValue} from "jotai";
import {gameReqAtom, listOfPlayers, PlayerInfo, connectedToWebSocket} from "../Atom.tsx";
import {useSetAtom} from "jotai";

// export type PlayerInfo = {
//     nickname: string;
//     isImpostor: boolean;
//     isHost: boolean;
// };




function RoomLobby() {
    const { code } = useParams<{ code: string }>();
    const [connected, setConnected] = useState(false);

    const navigate = useNavigate();
    const stompClient = useStompClient();
    const[players, setPlayers] = useAtom(listOfPlayers);

    const storedPlayer = sessionStorage.getItem("currentPlayer");
    const player = storedPlayer ? JSON.parse(storedPlayer) : null;

    const gameReq = useAtomValue(gameReqAtom);
    const setConnectedToWebSocket = useSetAtom(connectedToWebSocket);

    useEffect(() => {
        const storedPlayerRaw = sessionStorage.getItem("currentPlayer");
        const storedPlayer: PlayerInfo | null = storedPlayerRaw ? JSON.parse(storedPlayerRaw) : null;

        if (!storedPlayer || !stompClient || !connected) return;

        stompClient.publish({
            destination: `/app/room/${code}/players`,
            body: JSON.stringify({
                nickname: storedPlayer.nickname,
                isImpostor: false,
                isHost: storedPlayer.isHost,
            })
        });

        const syncTimeout = setTimeout(() => {
            stompClient.publish({
                destination: `/app/room/${code}/sync`,
                body: ""
            });
            console.log("📡 Sent sync request after reconnect");
        }, 300);

        return () => clearTimeout(syncTimeout);

    }, [stompClient, connected]);


    useSubscription(`/topic/room/${code}/start`, (message) => {
        console.log("game_started")
        const body = message.body;

        if (body === "GAME_STARTED") {
            navigate(`/room/${code}/round`);
        }
    });

    useSubscription(`/topic/room/${code}/kick/${player.nickname}`, (message) => {
        const body = message.body;
        alert("You have been removed from the room!");

        setTimeout(() => {
            if (stompClient && stompClient.connected) {
                stompClient.deactivate();
            }

            setConnectedToWebSocket(false);
            navigate("/");
        }, 1000);

    })

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
            await axios.post(`http://localhost:8080/api/round/${code}/start`,{
                rounds: gameReq.numberOfRounds,
                mode: gameReq.mode,
                maxPlayers: gameReq.maxNumbersOfPlayers,
                voteTime: gameReq.timeForVoting,
                roundsTime: gameReq.timeForRound
            });
        } catch (err) {
            console.error("Failed to start game", err);
            alert("Error starting the game.");
        }
    };
    return (
        <div className="min-h-screen bg-gray-900 text-white px-4 py-6 flex flex-col items-center">
            <div className="text-center mb-8">
                <RoomCode code={code} />
            </div>
            <div className="flex flex-row justify-center items-start gap-12 max-w-4xl w-full">
                {player?.isHost && (
                    <div className="flex-shrink-0 w-80">
                        <GameRoomSettings/>
                        <button
                            className={`font-semibold py-2 px-4 rounded mt-4 w-full transition
                                        ${players.length !== gameReq.maxNumbersOfPlayers
                                ? "bg-gray-500 cursor-not-allowed"
                                : "bg-green-600 hover:bg-green-700 text-white cursor-pointer"
                            }`}
                            onClick={handleStartGame}
                            disabled={players.length !== gameReq.maxNumbersOfPlayers}
                        >
                            Start Game
                        </button>
                    </div>
                )}

                <div className="flex-grow flex justify-center">
                    <PlayerList code={code} />
                </div>
            </div>
        </div>

    );
}

export default RoomLobby;
