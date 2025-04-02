import React, {useEffect, useState} from "react";

type GameRoomSetting = {
    mode: string;
    numberOfRounds: number;
    maxNumbersOfPlayers: number;
};
type GameRoomSettingsProps = {
    setGameMode: React.Dispatch<React.SetStateAction<GameRoomSetting>>;
};

 function GameRoomSettings({ setGameMode }: GameRoomSettingsProps) {
     const [rounds, setRounds] = useState(6);
     const [maxPlayers, setMaxPlayers] = useState(6);
     const [mode, setMode] = useState("STATIC_IMPOSTOR");

     useEffect(() => {
         setGameMode({
             mode,
             numberOfRounds: rounds,
             maxNumbersOfPlayers: maxPlayers,
         });
     }, [rounds, maxPlayers, mode]);

     return (
         <div className="bg-gray-800 p-4 rounded-lg shadow w-full">
             <h3 className="text-lg font-semibold mb-4">Game Settings</h3>

             {/* Mode select */}
             <div className="mb-4">
                 <label className="block mb-1">Game Mode:</label>
                 <select
                     value={mode}
                     onChange={(e) => setMode(e.target.value)}
                     className="w-full p-2 bg-gray-700 rounded"
                 >
                     <option value="STATIC_IMPOSTOR">Static Impostor</option>
                     <option value="ROTATING_IMPOSTOR">Rotating Impostor</option>
                     <option value="DOUBLE_AGENT">Double Agent</option>
                 </select>
             </div>

             {/* Rounds slider */}
             <div className="mb-4">
                 <label className="block mb-1">Rounds: {rounds}</label>
                 <input
                     type="range"
                     min={4}
                     max={20}
                     value={rounds}
                     onChange={(e) => setRounds(parseInt(e.target.value))}
                     className="w-full"
                 />
             </div>

             {/* Max players slider */}
             <div>
                 <label className="block mb-1">Max Players: {maxPlayers}</label>
                 <input
                     type="range"
                     min={3}
                     max={12}
                     value={maxPlayers}
                     onChange={(e) => setMaxPlayers(parseInt(e.target.value))}
                     className="w-full"
                 />
             </div>

         </div>
     );

 }

export default GameRoomSettings;
