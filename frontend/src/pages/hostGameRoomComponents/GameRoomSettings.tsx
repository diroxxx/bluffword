import React, { useState } from "react";

function GameRoomSettings() {
    const [rounds, setRounds] = useState(6);
    const [maxPlayers, setMaxPlayers] = useState(6);
    const [gameMode, setGameMode] = useState("STATIC_IMPOSTOR");

    return (
        <div className="min-h-screen bg-gray-900 text-white flex justify-center px-4 py-6">
            <div className="w-full max-w-md flex flex-col">
                <h3 className="text-2xl font-bold mb-6 text-center">Game Settings</h3>

                {/* Rounds */}
                <div className="mb-5">
                    <label className="block mb-1 font-medium">
                        Number of Rounds: {rounds}
                    </label>
                    <input
                        type="range"
                        min={4}
                        max={20}
                        value={rounds}
                        onChange={(e) => setRounds(parseInt(e.target.value))}
                        className="w-full accent-blue-500"
                    />
                </div>

                {/* Max Players */}
                <div className="mb-5">
                    <label className="block mb-1 font-medium">
                        Max Players: {maxPlayers}
                    </label>
                    <input
                        type="range"
                        min={2}
                        max={12}
                        value={maxPlayers}
                        onChange={(e) => setMaxPlayers(parseInt(e.target.value))}
                        className="w-full accent-green-500"
                    />
                </div>

                {/* Game Mode */}
                <div className="mb-2">
                    <label className="block mb-1 font-medium">Game Mode:</label>
                    <select
                        value={gameMode}
                        onChange={(e) => setGameMode(e.target.value)}
                        className="w-full bg-gray-700 text-white p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="STATIC_IMPOSTOR">Static Impostor</option>
                        <option value="ROTATING_IMPOSTOR">Rotating Impostor</option>
                        <option value="DOUBLE_AGENT">Double Agent</option>
                    </select>
                </div>
            </div>
        </div>
    );

}

export default GameRoomSettings;
