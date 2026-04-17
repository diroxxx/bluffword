import { useLocation, useNavigate } from "react-router-dom";
import { useListOfPlayers } from "../hooks/useListOfPlayers";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../atoms/playerInfoAtom";
import { useEffect, useState } from "react";
import { gameRoomAtom } from "../atoms/gameRoomAtom";
import type { GameSettings } from "../LobbyContainer/types/GameSettings";
import { getGameSettings } from "../LobbyContainer/api/getGameSettings";
import { useGameStateSetSocket } from "../shared/useGameStateSocket";
import { GameRoomState } from "../LobbyContainer/types/gameRoomState";
import { startGame } from "../round/api/startGame";

function LobbyPage() {
    const navigate = useNavigate();
    const [currentPlayer] = useAtom(playerInfoAtom);
    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);
    const [gameSettings, setGameSettings] = useState<GameSettings | null>(null);
    const [codeCopied, setCodeCopied] = useState(false);
    const [codeVisible, setCodeVisible] = useState(false);

    const handleCopyCode = () => {
        navigator.clipboard.writeText(currentPlayer?.roomCode || "");
        setCodeCopied(true);
        setTimeout(() => setCodeCopied(false), 2000);
    };

    const { connected, messages: playersResult, send: sendPlayersLists } = useListOfPlayers(currentPlayer?.roomCode);
    const { messages: stateResult } = useGameStateSetSocket(currentPlayer?.roomCode);

    useEffect(() => {
        setGameRoom(prev => ({ ...prev, players: playersResult[0] || [] }));
    }, [playersResult]);

    useEffect(() => {
        if (!stateResult[0]) return;
        setGameRoom(prev => ({ ...prev, state: stateResult[0] }));
        if (stateResult[0] === GameRoomState.ANSWERING || stateResult[0] === GameRoomState.CATEGORY_SELECTION) {
            navigate("/round", { state: { initialGameState: stateResult[0] } });
        }
    }, [stateResult]);

    useEffect(() => {
        sendPlayersLists({});
    }, [sendPlayersLists]);

    useEffect(() => {
        if (!currentPlayer?.roomCode) return;
        getGameSettings(currentPlayer.roomCode)
            .then(setGameSettings)
            .catch(console.error);
    }, [currentPlayer?.roomCode]);

    async function handleStartGame() {
        if (!currentPlayer?.id || !currentPlayer?.roomCode) return;
        await startGame(currentPlayer.roomCode, currentPlayer.id);
    }

    const settingLabels: Partial<Record<keyof GameSettings, string>> = {
        maxPlayers:        "Max Players",
        roundTotal:        "Total Rounds",
        numberOfImpostors: "Impostors",
        timeLimitAnswer:   "Answer Time (s)",
        timeLimitVote:     "Vote Time (s)",
        gameMode:          "Game Mode",
        categorySelectionMode: "Category Mode",
        staticCategory:    "Category",
    };

    if (!currentPlayer?.roomCode) {
        return (
            <div className="relative min-h-screen bg-deep-space-blue flex items-center justify-center">
                <span className="text-steel-blue/60 text-sm tracking-widest uppercase animate-breathe">
                    Loading...
                </span>
            </div>
        );
    }

    return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 pointer-events-none">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/15 via-deep-space-blue to-brick-red/15 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/8 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 py-10 gap-8">

                {/* Header */}
                <div className="text-center space-y-3">
                    <p className="text-steel-blue/60 text-xs tracking-[0.35em] uppercase">
                        {connected ? "Waiting for players" : "Connecting..."}
                    </p>
                    <h1 className="text-5xl md:text-6xl font-thin tracking-widest text-papaya-whip/90">
                        ROOM
                    </h1>
                    <div className="inline-flex items-center gap-3 px-5 py-2 bg-steel-blue/15 border border-steel-blue/35 rounded-xl">
                        <span className={`text-2xl text-papaya-whip tracking-[0.4em] transition-all duration-200 select-all ${codeVisible ? "" : "blur-sm"}`}>
                            {currentPlayer.roomCode}
                        </span>
                        <button
                            onClick={() => setCodeVisible(v => !v)}
                            title={codeVisible ? "Hide code" : "Show code"}
                            className="text-steel-blue/50 hover:text-papaya-whip/80 transition-colors duration-200"
                        >
                            {codeVisible ? (
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                                    <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                                    <line x1="1" y1="1" x2="23" y2="23"/>
                                </svg>
                            ) : (
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            )}
                        </button>
                        <button
                            onClick={handleCopyCode}
                            title="Copy code"
                            className="text-steel-blue/50 hover:text-papaya-whip/80 transition-colors duration-200"
                        >
                            {codeCopied ? (
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-teal-accent" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                            ) : (
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                                </svg>
                            )}
                        </button>
                    </div>
                </div>

                {/* Main content */}
                <div className="w-full max-w-4xl flex flex-col md:flex-row gap-5">

                    {/* Players + Start */}
                    <div className="flex-1 flex flex-col gap-4">
                        <div className="bg-deep-space-blue border border-steel-blue/30 rounded-2xl p-5">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-papaya-whip/80 text-sm tracking-[0.3em] uppercase">Players</h2>
                                <span className="text-steel-blue/60 text-xs tracking-wider">
                                    {gameRoom.players.length} joined
                                </span>
                            </div>
                            <div className="space-y-2">
                                {gameRoom.players.map((player) => (
                                    <div
                                        key={player.id}
                                        className="bg-steel-blue/10 border border-steel-blue/25 rounded-lg px-4 py-3 flex items-center justify-between"
                                    >
                                        <span className="text-papaya-whip/90 tracking-wide">{player.nickname}</span>
                                        {player.isHost && (
                                            <span className="text-brick-red/80 text-xs tracking-widest uppercase">Host</span>
                                        )}
                                    </div>
                                ))}
                                {gameRoom.players.length === 0 && (
                                    <p className="text-steel-blue/50 text-center py-5 tracking-widest text-sm animate-breathe">
                                        Waiting for players...
                                    </p>
                                )}
                            </div>
                        </div>

                        {currentPlayer.isHost && (
                            <button
                                onClick={handleStartGame}
                                className="w-full py-4 bg-brick-red hover:bg-molten-lava text-papaya-whip rounded-xl text-base tracking-widest uppercase font-medium transition-all duration-300 shadow-lg hover:shadow-brick-red/30 hover:-translate-y-0.5 active:translate-y-0"
                            >
                                Start Game
                            </button>
                        )}
                    </div>

                    {/* Settings */}
                    <div className="w-full md:w-72">
                        <div className="bg-deep-space-blue border border-steel-blue/30 rounded-2xl p-5">
                            <h2 className="text-papaya-whip/80 text-sm tracking-[0.3em] uppercase mb-4">Game Settings</h2>
                            {gameSettings ? (
                                <div className="space-y-2">
                                    {(Object.keys(settingLabels) as Array<keyof GameSettings>)
                                        .filter(key => gameSettings[key] !== undefined && gameSettings[key] !== null && gameSettings[key] !== "")
                                        .map((key) => (
                                        <div key={key} className="flex items-center justify-between py-2 border-b border-steel-blue/15 last:border-0">
                                            <span className="text-steel-blue/60 text-xs tracking-wider uppercase">
                                                {settingLabels[key]}
                                            </span>
                                            <span className="text-papaya-whip/85 text-sm tracking-wide">
                                                {typeof gameSettings[key] === 'boolean'
                                                    ? (gameSettings[key] ? 'Yes' : 'No')
                                                    : String(gameSettings[key])}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-steel-blue/50 text-center py-4 tracking-widest text-sm animate-breathe">
                                    Loading...
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
