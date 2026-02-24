import { useLocation } from "react-router-dom";
import { type ChangeEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../atoms/playerInfoAtom";
import { postCreateRoom } from "../LobbyContainer/api/postCreateRoom";
import { postJoinRoom } from "../LobbyContainer/api/postJoinRoom";
import type { GameSettings } from "../LobbyContainer/types/GameSettings";
import { GameMode, CategorySelectionMode } from "../LobbyContainer/types/GameSettings";
import { useCategoryNames } from "../LobbyContainer/hooks/useCategoryNames";

function EnterNamePage() {
    const location = useLocation();
    const { mode, code } = location.state || {};
    const [nickname, setNickname] = useState<string>("");
    const [maxPlayers, setMaxPlayers] = useState<number>(4);
    const [roundTotal, setRoundTotal] = useState<number>(5);
    const [timeLimitAnswer, setTimeLimitAnswer] = useState<number>(60);
    const [timeLimitVote, setTimeLimitVote] = useState<number>(30);
    const [gameMode, setGameMode] = useState<GameMode>(GameMode.STATIC_IMPOSTOR);
    const navigate = useNavigate();
    const [gameSettings, setGameSettings] = useState<GameSettings>({
        code: "",
        roundTotal: 5,
        maxPlayers: 4,
        numberOfImpostors: 1,
        timeLimitAnswer: 60,
        timeLimitVote: 30,
        gameMode: GameMode.STATIC_IMPOSTOR,
        categorySelectionMode: CategorySelectionMode.FIXED,
        staticCategory: undefined,
    });

    const [user, setUser] = useAtom(playerInfoAtom);
    const { data: categoryNames, isLoading, error } = useCategoryNames();


    const handleNicknameChange = (e: ChangeEvent<HTMLInputElement>) => {
        setNickname(e.target.value);
    };

    const handleSubmit = () => {
        if (!nickname.trim()) return alert("Enter your nickname first!");
        console.log({ mode, code, nickname });

        if (mode === "CREATE") {
            postCreateRoom(nickname, gameSettings).then((data) => {
            setUser(data);

            setTimeout(() => {
            navigate("/lobby");

        }, 10);
                
            }).catch((err) => {
                console.error("Error creating room:", err);
                alert("Failed to create room. Please try again.");
            });
        } else if (mode === "JOIN") {
            postJoinRoom(nickname, code).then((data) => {
                setUser(data);
                //some animations or loading screen can be added here
                navigate("/lobby");
            }).catch((err) => {
                console.error("Error joining room. Please check the room code and try again.");
            });

        }
    };

 return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 opacity-50">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/20 via-deep-space-blue to-brick-red/20 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/10 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="absolute inset-0 backdrop-blur-sm" />

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 gap-8 py-6">

                <div className="text-center space-y-4">
                    <h1 className="text-4xl md:text-6xl font-thin tracking-widest text-papaya-whip/90">
                        BLUFFWORD
                    </h1>

                    <p className="text-steel-blue/70 text-xs md:text-sm tracking-widest uppercase">
                        {mode === "CREATE" ? "Create a new room" : `Join room ${code}`}
                    </p>
                </div>

                <div className="w-full max-w-3xl space-y-4">
                    <input
                        type="text"
                        placeholder="ENTER YOUR NICKNAME"
                        value={nickname}
                        onChange={handleNicknameChange}
                        className="w-full px-5 py-3 bg-deep-space-blue/50 border border-steel-blue/30 text-papaya-whip/90 placeholder-steel-blue/50 rounded-xl focus:outline-none focus:border-papaya-whip/70 backdrop-blur-xl text-center tracking-widest text-base uppercase"
                    />

                    {mode === "CREATE" && (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* Max Players */}
                            <div className="space-y-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Max Players
                                </label>
                                <div className="flex gap-1.5 justify-center flex-wrap">
                                    {[4, 5, 6, 7, 8, 9, 10].map((num) => (
                                        <button
                                            key={num}
                                            onClick={() => setGameSettings(prev => ({ ...prev, maxPlayers: num }))}
                                            className={`w-10 h-10 rounded-lg text-sm font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                                gameSettings.maxPlayers === num
                                                    ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                    : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                            }`}
                                        >
                                            {num}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Round Total */}
                            <div className="space-y-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Total Rounds
                                </label>
                                <div className="flex gap-1.5 justify-center flex-wrap">
                                    {[3, 5, 7, 10, 15].map((num) => (
                                        <button
                                            key={num}
                                            onClick={() => setGameSettings(prev => ({ ...prev, roundTotal: num }))}
                                            className={`w-10 h-10 rounded-lg text-sm font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                                gameSettings.roundTotal === num
                                                    ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                    : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                            }`}
                                        >
                                            {num}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Time Limit Answer */}
                            <div className="space-y-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Answer Time (s)
                                </label>
                                <div className="flex gap-1.5 justify-center flex-wrap">
                                    {[30, 45, 60, 90, 120].map((num) => (
                                        <button
                                            key={num}
                                            onClick={() => setGameSettings(prev => ({ ...prev, timeLimitAnswer: num }))}
                                            className={`px-3 h-10 rounded-lg text-sm font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                                gameSettings.timeLimitAnswer === num
                                                    ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                    : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                            }`}
                                        >
                                            {num}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Time Limit Vote */}
                            <div className="space-y-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Vote Time (s)
                                </label>
                                <div className="flex gap-1.5 justify-center flex-wrap">
                                    {[15, 20, 30, 45, 60].map((num) => (
                                        <button
                                            key={num}
                                            onClick={() => setGameSettings(prev => ({ ...prev, timeLimitVote: num }))}
                                            className={`px-3 h-10 rounded-lg text-sm font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                                gameSettings.timeLimitVote === num
                                                    ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                    : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                            }`}
                                        >
                                            {num}
                                        </button>
                                    ))}
                                </div>

                                <div>
                                <label className="space-y-2 text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Number of Impostors
                                </label>
                                <div className="flex gap-1.5 justify-center flex-wrap">
                                    <input
                                        type="number"
                                        min={1}
                                        max={5}
                                        value={gameSettings.numberOfImpostors}
                                        onChange={(e) => setGameSettings(prev => ({ ...prev, numberOfImpostors: parseInt(e.target.value) }))}
                                        className="text-center w-16 h-10 rounded-lg text-sm font-medium tracking-wider transition-all duration-300 backdrop-blur-xl bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                    />
                                </div>
                            </div>
                            </div>

                            {/* Game Mode */}
                            <div className="space-y-2 md:col-span-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Game Mode
                                </label>
                                <div className="flex gap-2 justify-center flex-wrap">
                                    <button
                                        onClick={() => setGameSettings(prev => ({ ...prev, gameMode: "ROUND_IMPOSTOR" }))}
                                        className={`px-5 py-2.5 rounded-lg text-xs font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                            gameSettings.gameMode === "ROUND_IMPOSTOR"
                                                ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                        }`}
                                    >
                                        ROUND IMPOSTOR
                                    </button>
                                    <button
                                        onClick={() => setGameSettings(prev => ({ ...prev, gameMode: "STATIC_IMPOSTOR" }))}
                                        className={`px-5 py-2.5 rounded-lg text-xs font-medium tracking-wider transition-all duration-300 backdrop-blur-xl ${
                                            gameSettings.gameMode === "STATIC_IMPOSTOR"
                                                ? "bg-papaya-whip/20 text-papaya-whip border-2 border-papaya-whip/70"
                                                : "bg-deep-space-blue/30 text-steel-blue/70 border border-steel-blue/30 hover:border-papaya-whip/50 hover:text-papaya-whip/70"
                                        }`}
                                    >
                                        STATIC IMPOSTOR
                                    </button>
                                </div>
                            </div>

                            {/* Category Selection Mode */}
                            <div className="space-y-2 md:col-span-2">
                                <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                    Category Selection Mode
                                </label>
                                <select
                                    value={gameSettings.categorySelectionMode}
                                    onChange={e =>
                                        setGameSettings(prev => ({
                                            ...prev,
                                            categorySelectionMode: e.target.value as CategorySelectionMode,
                                            staticCategory: undefined,
                                        }))
                                    }
                                    className="w-full px-4 py-2 rounded-lg bg-deep-space-blue/50 border border-steel-blue/30 text-papaya-whip/90"
                                >
                                    <option value={CategorySelectionMode.FIXED}>Fixed</option>
                                    <option value={CategorySelectionMode.RANDOM_PER_ROUND}>Random per round</option>
                                    <option value={CategorySelectionMode.PLAYER_CHOSEN_PER_ROUND}>Player chosen per round</option>
                                </select>
                            </div>

                            {/* Static Category select if mode is FIXED */}
                            {gameSettings.categorySelectionMode === CategorySelectionMode.FIXED && (
                                <div className="space-y-2 md:col-span-2">
                                    <label className="text-steel-blue/70 text-xs tracking-wider uppercase text-center block">
                                        Static Category
                                    </label>
                                    <select
                                        value={gameSettings.staticCategory ?? ""}
                                        onChange={e =>
                                            setGameSettings(prev => ({
                                                ...prev,
                                                staticCategory: e.target.value,
                                            }))
                                        }
                                        className="w-full px-4 py-2 rounded-lg bg-deep-space-blue/50 border border-steel-blue/30 text-papaya-whip/90"
                                    >
                                        <option value="" disabled>
                                            Select category
                                        </option>
                                        {Array.isArray(categoryNames) && categoryNames.map((cat: string) => (
                                            <option key={cat} value={cat}>
                                                {cat}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            )}
                        </div>
                    )}

                    <button
                        onClick={handleSubmit}
                        className="w-full py-3 bg-papaya-whip/10 hover:bg-papaya-whip/20 text-papaya-whip border border-papaya-whip/30 rounded-xl text-base font-medium tracking-wider transition-all duration-300 backdrop-blur-xl"
                    >
                        {mode === "CREATE" ? "CREATE ROOM" : "JOIN ROOM"}
                    </button>

                    <button
                        onClick={() => navigate("/")}
                        className="w-full py-2 text-steel-blue/70 hover:text-papaya-whip/70 text-xs tracking-wider transition-all duration-300"
                    >
                        ← BACK
                    </button>
                </div>

            </div>
        </div>
    );
}

export default EnterNamePage;