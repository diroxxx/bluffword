import { useLocation, useNavigate } from "react-router-dom";
import { useListOfPlayers } from "../hooks/useListOfPlayers";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../atoms/playerInfoAtom";
import { use, useEffect, useState } from "react";
import { gameRoomAtom } from "../atoms/gameRoomAtom";
import type { GameSettings } from "../LobbyContainer/types/GameSettings";
import { getGameSettings } from "../LobbyContainer/api/getGameSettings";
import { useGameStateSetSocket } from "../shared/useGameStateSocket";
import { GameRoomState } from "../LobbyContainer/types/gameRoomState";
import { useStartRound } from "../round/hooks/useStartRound";

function LobbyPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { code } = location.state || {};




    const [currentPlayer, setPlayer] = useAtom(playerInfoAtom);
    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);


     if (!currentPlayer?.roomCode) {
        return <div>Loading...</div>;
    }

    const [gameSettings, setGameSettings] = useState<GameSettings | null>(null);

    const { connected, messages: playersResult, send: sendPlayersLists } = useListOfPlayers(currentPlayer?.roomCode);
    
    const { connected: stateConnected, messages: stateResult, send: sendState } = useGameStateSetSocket(currentPlayer?.roomCode);

    const { connected: nextWordConnected, messages: word, send: startRound } = useStartRound(currentPlayer?.roomCode, currentPlayer?.id);
    
    function handleDeleteUser() {

     }


    useEffect(() => {

        setGameRoom( prev => ({
            ...prev,
            players: playersResult[0] || [],
        }));
    }, [playersResult]);

    useEffect(() => {
        setGameRoom((prev) => ({
            ...prev,
            state: stateResult[0] || GameRoomState.LOBBY
        }));

        if (stateResult[0] === GameRoomState.ANSWERING) {
            navigate("/round", { state: { roomCode: currentPlayer?.roomCode } });
        }

    }, [stateResult]);


    function sendStartGame() { 
        if (!currentPlayer?.roomCode) {
            console.error("No room code available to start the game.");
            return;
        }
            try {
                startRound({});
                navigate("/round", { state: { roomCode: currentPlayer?.roomCode } });
                console.log("Start game request sent successfully");
            } catch (error) {
                console.error("Error sending start game request:", error);
            }
    }
    
    useEffect(() => {
        sendPlayersLists({});
        if ( gameRoom.players.length === 0) {
            sendPlayersLists({});
        }
    }, [currentPlayer?.roomCode, sendPlayersLists]);

    useEffect(() => {
        if (currentPlayer?.roomCode) {
            setGameRoom(prev => ({
                ...prev,
                roomCode: currentPlayer.roomCode,
            }));
        }
    }, [currentPlayer?.roomCode]);

       
    useEffect(() => {
        if (!currentPlayer?.roomCode) return;
        const fetchSettings = async () => {
            try {
                const settings = await  getGameSettings(currentPlayer?.roomCode!!);
                setGameSettings(settings);
            } catch (error) {
                console.error("Error fetching game settings:", error);
            }
        };
        fetchSettings();

    }, [currentPlayer?.roomCode]);

    return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 opacity-50">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/20 via-deep-space-blue to-brick-red/20 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/10 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="absolute inset-0 backdrop-blur-sm" />

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 gap-16">

                <div className="text-center space-y-8">
                    <h1 className="text-6xl md:text-8xl font-thin tracking-widest text-papaya-whip/90">
                        ROOM {currentPlayer?.roomCode}
                    </h1>

                    <p className="text-steel-blue/70 text-sm md:text-base tracking-widest uppercase">
                        {connected ? "Connected" : "Connecting..."}
                    </p>
                </div>

                <div className="w-full max-w-4xl flex flex-col md:flex-row gap-6">
                    <div className="flex-1 space-y-6">
                        <div className="bg-deep-space-blue/50 border border-steel-blue/30 rounded-xl p-6 backdrop-blur-xl">
                            <h2 className="text-papaya-whip/90 text-xl tracking-wider mb-4 text-center">
                                PLAYERS ({gameRoom.players.length})
                            </h2>

                            <div className="space-y-3">
                                {gameRoom.players.map((player) => (
                                    <div
                                        key={player.id}
                                        className="bg-deep-space-blue/30 border border-steel-blue/20 rounded-lg px-4 py-3 flex items-center justify-between"
                                    >
                                        <span className="text-papaya-whip/90 tracking-wide">
                                            {player.nickname}
                                        </span>
                                        {player.isHost && (
                                            <span className="text-brick-red text-xs tracking-wider">
                                                HOST
                                            </span>
                                        )}
                                    </div>
                                ))}

                                {gameRoom.players.length === 0 && (
                                    <p className="text-steel-blue/50 text-center py-4 tracking-wide">
                                        Waiting for players...
                                    </p>
                                )}
                            </div>
                        </div>

                        {currentPlayer?.isHost && (
                            <button
                                className="w-full py-4 bg-papaya-whip/10 hover:bg-papaya-whip/20 text-papaya-whip border border-papaya-whip/30 rounded-xl text-lg font-medium tracking-wider transition-all duration-300 backdrop-blur-xl"
                                onClick={() => {
                                  navigate("/round", { state: { roomCode: currentPlayer?.roomCode } });  
                                
                                  sendStartGame();
                                
                                }}
                            >
                                START GAME
                            </button>
                        )}
                    </div>

                    {/* Game Settings Panel */}
                    <div className="w-full md:w-80 space-y-6">
                        <div className="bg-deep-space-blue/50 border border-steel-blue/30 rounded-xl p-6 backdrop-blur-xl">
                            <h2 className="text-papaya-whip/90 text-xl tracking-wider mb-4 text-center">
                                GAME SETTINGS
                            </h2>

                            {gameSettings ? (
                                <div className="space-y-4">
                                    {Object.entries(gameSettings).map(([key, value]) => (
                                        <div
                                            key={key}
                                            className="bg-deep-space-blue/30 border border-steel-blue/20 rounded-lg px-4 py-3"
                                        >
                                            <div className="text-steel-blue/70 text-xs tracking-wider uppercase mb-1">
                                                {key.replace(/([A-Z])/g, ' $1').trim()}
                                            </div>
                                            <div className="text-papaya-whip/90 tracking-wide">
                                                {typeof value === 'boolean' ? (value ? 'Yes' : 'No') : value}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-steel-blue/50 text-center py-4 tracking-wide">
                                    Loading settings...
                                </p>
                            )}
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
}
export default LobbyPage;