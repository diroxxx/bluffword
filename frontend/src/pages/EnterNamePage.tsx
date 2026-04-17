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

const OPTION_BTN = (active: boolean) =>
    `h-10 px-3 rounded-lg text-sm tracking-wider transition-all duration-200 border ${
        active
            ? "bg-steel-blue/30 text-papaya-whip border-steel-blue/70"
            : "bg-deep-space-blue text-steel-blue/70 border-steel-blue/25 hover:border-steel-blue/60 hover:text-papaya-whip/80"
    }`;

function EnterNamePage() {
    const location = useLocation();
    const { mode, code } = location.state || {};
    const [nickname, setNickname] = useState<string>("");
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

    const navigate = useNavigate();
    const [, setUser] = useAtom(playerInfoAtom);
    const { data: categoryNames } = useCategoryNames();

    const handleSubmit = () => {
        if (!nickname.trim()) return alert("Enter your nickname first!");

        if (mode === "CREATE") {
            postCreateRoom(nickname, gameSettings)
                .then((data) => {
                    setUser(data);
                    setTimeout(() => navigate("/lobby"), 10);
                })
                .catch(() => alert("Failed to create room. Please try again."));
        } else if (mode === "JOIN") {
            postJoinRoom(nickname, code)
                .then((data) => {
                    setUser(data);
                    navigate("/lobby");
                })
                .catch(console.error);
        }
    };

    return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 pointer-events-none">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/15 via-deep-space-blue to-brick-red/15 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/8 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 gap-6 py-10">

                {/* Header */}
                <div className="text-center space-y-2">
                    <h1 className="text-4xl md:text-5xl font-thin tracking-widest text-papaya-whip/90">
                        BLUFFWORD
                    </h1>
                    <p className="text-steel-blue/60 text-xs tracking-[0.35em] uppercase">
                        {mode === "CREATE" ? "Create a new room" : `Join room ${code}`}
                    </p>
                </div>

                <div className="w-full max-w-2xl flex flex-col gap-4">

                    {/* Nickname input */}
                    <input
                        autoFocus
                        type="text"
                        placeholder="Your nickname"
                        value={nickname}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setNickname(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
                        className="w-full px-5 py-3.5 bg-deep-space-blue border border-steel-blue/35 text-papaya-whip/90 placeholder-steel-blue/40 rounded-xl focus:outline-none focus:border-steel-blue/70 focus:ring-2 focus:ring-steel-blue/20 text-center tracking-widest text-base transition-all duration-200"
                    />

                    {/* Settings (CREATE only) */}
                    {mode === "CREATE" && (
                        <div className="bg-deep-space-blue border border-steel-blue/30 rounded-2xl p-5 flex flex-col gap-5">

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

                                {/* Max Players */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Max Players
                                    </label>
                                    <div className="flex gap-1.5 flex-wrap">
                                        {[4, 5, 6, 7, 8, 9, 10].map((num) => (
                                            <button
                                                key={num}
                                                onClick={() => setGameSettings(prev => ({ ...prev, maxPlayers: num }))}
                                                className={`w-10 ${OPTION_BTN(gameSettings.maxPlayers === num)}`}
                                            >
                                                {num}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Total Rounds */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Total Rounds
                                    </label>
                                    <div className="flex gap-1.5 flex-wrap">
                                        {[3, 5, 7, 10, 15].map((num) => (
                                            <button
                                                key={num}
                                                onClick={() => setGameSettings(prev => ({ ...prev, roundTotal: num }))}
                                                className={`w-10 ${OPTION_BTN(gameSettings.roundTotal === num)}`}
                                            >
                                                {num}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Answer Time */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Answer Time (s)
                                    </label>
                                    <div className="flex gap-1.5 flex-wrap">
                                        {[30, 45, 60, 90, 120].map((num) => (
                                            <button
                                                key={num}
                                                onClick={() => setGameSettings(prev => ({ ...prev, timeLimitAnswer: num }))}
                                                className={OPTION_BTN(gameSettings.timeLimitAnswer === num)}
                                            >
                                                {num}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Vote Time */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Vote Time (s)
                                    </label>
                                    <div className="flex gap-1.5 flex-wrap">
                                        {[15, 20, 30, 45, 60].map((num) => (
                                            <button
                                                key={num}
                                                onClick={() => setGameSettings(prev => ({ ...prev, timeLimitVote: num }))}
                                                className={OPTION_BTN(gameSettings.timeLimitVote === num)}
                                            >
                                                {num}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Number of Impostors */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Impostors
                                    </label>
                                    <div className="flex gap-1.5 flex-wrap">
                                        {[1, 2, 3, 4, 5].map((num) => (
                                            <button
                                                key={num}
                                                onClick={() => setGameSettings(prev => ({ ...prev, numberOfImpostors: num }))}
                                                className={`w-10 ${OPTION_BTN(gameSettings.numberOfImpostors === num)}`}
                                            >
                                                {num}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Game Mode */}
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Game Mode
                                    </label>
                                    <div className="flex gap-2 flex-wrap">
                                        <button
                                            onClick={() => setGameSettings(prev => ({ ...prev, gameMode: GameMode.ROUND_IMPOSTOR }))}
                                            className={`px-4 ${OPTION_BTN(gameSettings.gameMode === GameMode.ROUND_IMPOSTOR)}`}
                                        >
                                            Round
                                        </button>
                                        <button
                                            onClick={() => setGameSettings(prev => ({ ...prev, gameMode: GameMode.STATIC_IMPOSTOR }))}
                                            className={`px-4 ${OPTION_BTN(gameSettings.gameMode === GameMode.STATIC_IMPOSTOR)}`}
                                        >
                                            Static
                                        </button>
                                    </div>
                                </div>

                            </div>

                            {/* Category Selection — full width */}
                            <div className="border-t border-steel-blue/15 pt-4 space-y-3">
                                <div className="space-y-2">
                                    <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                        Category Mode
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
                                        className="w-full px-4 py-2.5 rounded-lg bg-deep-space-blue border border-steel-blue/35 text-papaya-whip/90 scheme-dark focus:outline-none focus:border-steel-blue/60 transition-all duration-200"
                                    >
                                        <option value={CategorySelectionMode.FIXED}>Fixed</option>
                                        <option value={CategorySelectionMode.RANDOM_PER_ROUND}>Random per round</option>
                                        <option value={CategorySelectionMode.PLAYER_CHOSEN_PER_ROUND}>Player chosen per round</option>
                                    </select>
                                </div>

                                {gameSettings.categorySelectionMode === CategorySelectionMode.FIXED && (
                                    <div className="space-y-2">
                                        <label className="text-steel-blue/60 text-xs tracking-[0.3em] uppercase block">
                                            Category
                                        </label>
                                        <select
                                            value={gameSettings.staticCategory ?? ""}
                                            onChange={e =>
                                                setGameSettings(prev => ({
                                                    ...prev,
                                                    staticCategory: e.target.value,
                                                }))
                                            }
                                            className="w-full px-4 py-2.5 rounded-lg bg-deep-space-blue border border-steel-blue/35 text-papaya-whip/90 scheme-dark focus:outline-none focus:border-steel-blue/60 transition-all duration-200"
                                        >
                                            <option value="" disabled>Select category</option>
                                            {Array.isArray(categoryNames) && categoryNames.map((cat: string) => (
                                                <option key={cat} value={cat}>{cat}</option>
                                            ))}
                                        </select>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}

                    {/* Submit */}
                    <button
                        onClick={handleSubmit}
                        className="w-full py-3.5 bg-brick-red hover:bg-molten-lava text-papaya-whip rounded-xl text-base tracking-widest uppercase font-medium transition-all duration-300 shadow-lg hover:shadow-brick-red/30 hover:-translate-y-0.5 active:translate-y-0"
                    >
                        {mode === "CREATE" ? "Create Room" : "Join Room"}
                    </button>

                    <button
                        onClick={() => navigate("/")}
                        className="w-full py-2 text-steel-blue/50 hover:text-papaya-whip/60 text-xs tracking-widest uppercase transition-all duration-200"
                    >
                        ← Back
                    </button>
                </div>

            </div>
        </div>
    );
}

export default EnterNamePage;
