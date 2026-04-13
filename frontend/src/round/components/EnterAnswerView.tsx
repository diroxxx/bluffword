import { useStartRound } from "../hooks/useStartRound";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import { useEffect, useState } from "react";
import { useSendAnswer } from "../webSocketsHooks/useSendAnswer";

export function EnterAnswerView() {
    const [player] = useAtom(playerInfoAtom);
    const [, setGameRoom] = useAtom(gameRoomAtom);

    const [currentRound, setCurrentRound] = useState<number>(1);
    const [answerText, setAnswerText] = useState<string>("");
    const [submitted, setSubmitted] = useState<boolean>(false);

    const { connected: wordConnected, messages: word, send: sendWord } = useStartRound(player?.roomCode, player?.id);
    const { messages: time } = useTimerRoundStomp(player?.roomCode || "", "ANSWERING");
    const { send: sendAnswers } = useSendAnswer(player?.roomCode || "", player?.id || "", currentRound);

    useEffect(() => {
        if (wordConnected) sendWord({});
    }, [wordConnected]);

    useEffect(() => {
        if (!word[0]) return;
        setCurrentRound(word[0].currentRound);
        setAnswerText("");
        setSubmitted(false);
        setGameRoom(prev => ({ ...prev, currentRound: word[0].currentRound }));
    }, [word]);

    return (
        <div className="flex flex-col items-center gap-5 max-w-2xl w-full">

            <div className="flex items-center justify-between w-full gap-4">
                <div className="bg-deep-space-blue/95 border-2 border-steel-blue/30 rounded-2xl shadow-xl px-6 py-4 flex-1">
                    <h2 className="text-2xl text-papaya-whip tracking-widest text-center">ROUND {currentRound}</h2>
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
                ${word[0]?.isImpostor ? "bg-brick-red/30 border-brick-red" : "bg-teal-600/30 border-teal-500"}`}>
                <span className={`text-xl tracking-wider
                    ${word[0]?.isImpostor ? "text-brick-red drop-shadow-lg" : "text-teal-300 drop-shadow-lg"}`}>
                    {word[0]?.isImpostor ? "IMPOSTOR" : "CIVILIAN"}
                </span>
            </div>

            <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-10 py-10 text-center">
                <span className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-4">Your word</span>
                <span className="text-5xl md:text-6xl text-papaya-whip bg-steel-blue/40 px-10 py-6 rounded-2xl shadow-inner tracking-widest select-all inline-block border border-steel-blue/50">
                    {word[0]?.word ?? <span className="text-steel-blue/50">Loading...</span>}
                </span>
            </div>

            {submitted ? (
                <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-6 py-8 text-center">
                    <span className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-3">Your answer</span>
                    <span className="text-xl text-papaya-whip/80 tracking-wider">{answerText}</span>
                    <div className="mt-6 flex items-center justify-center gap-3">
                        <div className="w-2 h-2 rounded-full bg-steel-blue/60 animate-bounce" style={{ animationDelay: "0ms" }} />
                        <div className="w-2 h-2 rounded-full bg-steel-blue/60 animate-bounce" style={{ animationDelay: "150ms" }} />
                        <div className="w-2 h-2 rounded-full bg-steel-blue/60 animate-bounce" style={{ animationDelay: "300ms" }} />
                        <span className="text-steel-blue/60 text-sm uppercase tracking-widest ml-2">Waiting for others</span>
                    </div>
                </div>
            ) : (
                <>
                    <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-6 py-6">
                        <label className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-3">Your answer</label>
                        <input
                            value={answerText}
                            onChange={(e) => setAnswerText(e.target.value)}
                            type="text"
                            className="w-full px-6 py-4 rounded-xl bg-steel-blue/30 border-2 border-steel-blue/50 text-papaya-whip text-lg placeholder-papaya-whip/40 focus:outline-none focus:ring-2 focus:ring-papaya-whip focus:border-papaya-whip transition-all duration-300"
                            placeholder="Type your answer here..."
                        />
                    </div>

                    <button
                        disabled={!word[0] || !answerText.trim()}
                        onClick={() => {
                            if (answerText.trim()) {
                                sendAnswers(answerText.trim());
                                setSubmitted(true);
                            }
                        }}
                        className="w-full bg-green-600 hover:bg-green-700 disabled:opacity-40 disabled:cursor-not-allowed text-papaya-whip border border-papaya-whip/30 rounded-xl text-lg tracking-wider transition-all duration-300 px-6 py-4"
                    >
                        Submit
                    </button>
                </>
            )}
        </div>
    );
}
