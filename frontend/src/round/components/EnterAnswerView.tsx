import { useStartRound } from "../hooks/useStartRound";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import { useEffect, useState } from "react";
import { useRoundAnswers } from "../webSocketsHooks/useRoundAnswers";



export function EnterAnswerView() {

    const [player, setPlayer] = useAtom(playerInfoAtom);
    
    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);
          
    const { connected: wordConnected, messages: word, send: sendWord } = useStartRound(player?.roomCode, player?.id, gameRoom.currentRound);
    
    const { connected: timerConnected, messages: time, send: sendTime } = useTimerRoundStomp(player?.roomCode || "", "ANSWERING");

    const { connected: answersConnected, messages: answers, send: sendAnswers } = useRoundAnswers(player?.roomCode!, gameRoom.currentRound!, player?.id!);
    
    const [answerObj, setAnswerObj] = useState<string>();

    useEffect(() => {
        console.log("Word received:", word);
        setGameRoom( prev => ({
            ...prev,
            currentRound: word[0]?.currentRound || 1,
        }));
    }, [word]);

    useEffect(() => {
        console.log("Time received:", time);
    }, [time]);


    return (
        <div className="flex flex-col items-center gap-5 max-w-2xl w-full">

            <div className="flex items-center justify-between w-full gap-4">
                <div className="bg-deep-space-blue/95 border-2 border-steel-blue/30 rounded-2xl shadow-xl px-6 py-4 flex-1">
                    <h2 className="text-2xl text-papaya-whip tracking-widest text-center">ROUND {gameRoom.currentRound}</h2>
                </div>
                
                <div className="bg-deep-space-blue/95 border-2 border-brick-red/40 rounded-2xl shadow-xl px-6 py-4">
                    <div className="flex items-center gap-3">
                        <span className="text-papaya-whip/70 text-sm uppercase tracking-wide">Time:</span>
                        <span className="text-3xl text-papaya-whip bg-brick-red/40 px-4 py-1 rounded-lg border border-brick-red/50">
                            {time[0] && time[0] > 0 ? time[0] : "..."}
                        </span>
                    </div>
                </div>
            </div>
            <div className={`w-full rounded-2xl shadow-xl px-8 py-6 text-center border-2
                            ${word[0]?.isImpostor
                                ? "bg-brick-red/30 border-brick-red"
                                : "bg-teal-600/30 border-teal-500"
                            }`
                        }>
                            <span className={`text-xl tracking-wider
                                ${word[0]?.isImpostor ? "text-brick-red drop-shadow-lg" : "text-teal-300 drop-shadow-lg"}`
                            }>
                                {word[0]?.isImpostor ? "IMPOSTOR" : "CIVILIAN"}
                            </span>
                        </div>

            <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-10 py-10 text-center">
                <span className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-4">Your word</span>
                <span className="text-5xl md:text-6xl text-papaya-whip bg-steel-blue/40 px-10 py-6 rounded-2xl shadow-inner tracking-widest select-all inline-block border border-steel-blue/50">
                    {word && word.length > 0 ? word[0]?.word : <span className="text-steel-blue/50">Loading...</span>}
                </span>
            </div>

                        <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-6 py-6">
                <label className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-3">Your answer</label>
                <input 
                    value={answerObj || ""}
                    onChange={(e) => setAnswerObj(e.target.value)}
                    type="text" 
                    className="w-full px-6 py-4 rounded-xl bg-steel-blue/30 border-2 border-steel-blue/50 text-papaya-whip text-lg placeholder-papaya-whip/40 focus:outline-none focus:ring-2 focus:ring-papaya-whip focus:border-papaya-whip transition-all duration-300" 
                    placeholder="Type your answer here..." 
                />
            </div>
            <input type="submit" 
                value="button"
                onClick={() => {
                    console.log("Submitting answer:", answerObj);
                    if (answerObj && answerObj.trim() !== "") {

                        sendAnswers(answerObj.trim());
                    }
                }} 
                className="w-full bg-green-600 hover:bg-green-700 text-papaya-whip border border-papaya-whip/30 rounded-xl text-lg tracking-wider transition-all duration-300 px-6 py-4 cursor-pointer backdrop-blur-xl"
            />
        </div>
    );
}