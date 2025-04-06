import {atom, useAtom} from "jotai";
import {gameReqAtom, listOfPlayers, modes,} from "../../Atom.tsx";
import {useAtomValue} from "jotai/index";
import {useEffect, useState} from "react";
import axios from "axios";
function GameRoomSettings( ) {

    const [gameReq, setGameReq] = useAtom(gameReqAtom);
    const players = useAtomValue(listOfPlayers);
    const [listModes, setListModes] = useAtom(modes);

    useEffect(() => {
        const fetchGameModes = async () => {
            try {
                const res = await axios("http://localhost:8080/api/gameRoom/GameModes");
                const data = await res.data;
                setListModes(data);
            } catch (err) {
                console.error("Błąd:", err);
            }
        };

        fetchGameModes();
    }, []);


    const updateNumberOfRounds = (newRounds: number) => {
        setGameReq((prev) => ({
            ...prev,
            numberOfRounds: newRounds,
        }));
    };
    const updateMaxPlayers = (numbersOfPlayers: number) => {
        setGameReq((prev) => ({
            ...prev,
            maxNumbersOfPlayers: numbersOfPlayers,
        }));
    };
    const updateMode = (mode: string) => {
        setGameReq((prev) => ({
            ...prev,
            mode: mode
        }));
    };
    const updateTimeToVote = (timeForVote: number) => {
        setGameReq((prev) => ({
            ...prev,
            timeForVoting: timeForVote,
        }));
    };
    const updateTimeForRound = (timeForRound: number) => {
        setGameReq((prev) => ({
            ...prev,
            timeForRound: timeForRound
        }));
    };

     return (
         <div className="bg-gray-800 p-4 rounded-lg shadow w-full">
             <h3 className="text-lg font-semibold mb-4">Game Settings</h3>

             {/* Mode select */}
             <div className="mb-4">
                 <label className="block mb-1">Game Mode:</label>
                 <select
                     value={gameReq.mode}
                     onChange={(e) => updateMode(e.target.value)}
                     className="w-full p-2 bg-gray-700 rounded"
                 >
                     {listModes.map((mode) => (
                         <option key={mode} value={mode}>
                             {mode}
                         </option>
                     ))}
                 </select>
             </div>

             {/* Rounds slider */}
             <div className="mb-4">
                 <label className="block mb-1">Rounds: {gameReq.numberOfRounds}</label>
                 <input
                     type="range"
                     min={4}
                     max={20}
                     value={gameReq.numberOfRounds}
                     onChange={(e) => updateNumberOfRounds(parseInt(e.target.value))}
                     className="w-full"
                 />
             </div>

             {/* Max players slider */}
             <div>
                 <label className="block mb-1">Max Players: {gameReq.maxNumbersOfPlayers}</label>
                 <input
                     type="range"
                     min={Math.max(players.length, 3)}
                     max={12}
                     value={gameReq.maxNumbersOfPlayers}
                     onChange={(e) => updateMaxPlayers(parseInt(e.target.value))}
                     className="w-full accent-green-500"
                 />
             </div>


             {/* Time for Voting */}
             <div>
                 <label className="block mb-1 font-medium">Voting Time: {gameReq.timeForVoting} sec</label>
                 <input
                     type="range"
                     min={30}
                     max={120}
                     // step={5}
                     value={gameReq.timeForVoting}
                     onChange={(e) => updateTimeToVote(Number(e.target.value))}
                     className="w-full accent-red-500"
                 />
             </div>

             {/* Time for Round */}
             <div>
                 <label className="block mb-1 font-medium">Round Time: {gameReq.timeForRound} sec</label>
                 <input
                     type="range"
                     min={10}
                     max={180}
                     step={5}
                     value={gameReq.timeForRound}
                     onChange={(e) =>
                         updateTimeForRound(Number(e.target.value))}
                     className="w-full accent-yellow-500"
                 />
             </div>

         </div>
     );
 }

export default GameRoomSettings;
