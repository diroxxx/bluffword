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

    const isImpostor = word[0]?.isImpostor ?? false;

    const handleSubmit = () => {
        if (word[0] && answerText.trim()) {
            sendAnswers(answerText.trim());
            setSubmitted(true);
        }
    };

    return (
        <div className="flex flex-col items-center gap-4 max-w-xl w-full">

            {/* Header bar: round + role badge + timer */}
            <div className="w-full flex items-center gap-3">
                <div className="bg-deep-space-blue border border-steel-blue/30 rounded-xl px-5 py-3 flex items-center gap-3 flex-1">
                    <span className="text-steel-blue/60 text-xs uppercase tracking-widest">Round</span>
                    <span className="text-papaya-whip text-2xl tracking-widest">{currentRound}</span>
                </div>

                <div className={`rounded-xl px-5 py-3 border text-center
                    ${isImpostor
                        ? "bg-brick-red/20 border-brick-red/60"
                        : "bg-teal-600/15 border-teal-500/50"
                    }`}>
                    <span className={`text-sm font-bold tracking-[0.2em] uppercase
                        ${isImpostor ? "text-brick-red" : "text-teal-300"}`}>
                        {isImpostor ? "Impostor" : "Civilian"}
                    </span>
                </div>

                <div className="bg-deep-space-blue border border-brick-red/40 rounded-xl px-5 py-3 flex items-center gap-2">
                    <span className="text-papaya-whip/50 text-xs uppercase tracking-widest">⏱</span>
                    <span className={`text-2xl tabular-nums tracking-wider
                        ${(time[0] ?? 99) <= 10 ? "text-brick-red animate-breathe" : "text-papaya-whip"}`}>
                        {time[0] && time[0] > 0 ? time[0] : "—"}
                    </span>
                </div>
            </div>

            {/* Word reveal */}
            <div className="w-full bg-deep-space-blue border border-steel-blue/35 rounded-2xl shadow-lg px-8 py-8 text-center">
                <span className="block text-steel-blue/60 text-xs uppercase tracking-[0.3em] mb-5">Your word</span>
                <span className={`text-4xl md:text-5xl text-papaya-whip tracking-widest select-all inline-block px-8 py-4 rounded-xl border shadow-inner
                    ${isImpostor
                        ? "bg-brick-red/15 border-brick-red/30"
                        : "bg-steel-blue/25 border-steel-blue/40"
                    }`}>
                    {word[0]?.word ?? <span className="text-steel-blue/40 text-2xl">Loading...</span>}
                </span>
                {isImpostor && (
                    <p className="mt-4 text-brick-red/60 text-xs tracking-widest uppercase">
                        You don't know the real word — bluff your way through!
                    </p>
                )}
            </div>

            {/* Answer section */}
            {submitted ? (
                <div className="w-full bg-deep-space-blue border border-steel-blue/30 rounded-2xl px-6 py-7 text-center fade-slide-up">
                    <span className="block text-steel-blue/60 text-xs uppercase tracking-[0.3em] mb-2">Submitted</span>
                    <span className="text-xl text-papaya-whip/90 tracking-wide">{answerText}</span>
                    <div className="mt-5 flex items-center justify-center gap-2">
                        <div className="w-1.5 h-1.5 rounded-full bg-steel-blue/50 animate-bounce" style={{ animationDelay: "0ms" }} />
                        <div className="w-1.5 h-1.5 rounded-full bg-steel-blue/50 animate-bounce" style={{ animationDelay: "150ms" }} />
                        <div className="w-1.5 h-1.5 rounded-full bg-steel-blue/50 animate-bounce" style={{ animationDelay: "300ms" }} />
                        <span className="text-steel-blue/50 text-xs uppercase tracking-widest ml-2">Waiting for others</span>
                    </div>
                </div>
            ) : (
                <div className="w-full bg-deep-space-blue border border-steel-blue/30 rounded-2xl px-6 py-6 flex flex-col gap-4">
                    <label className="block text-steel-blue/60 text-xs uppercase tracking-[0.3em]">Your answer</label>
                    <input
                        autoFocus
                        value={answerText}
                        onChange={(e) => setAnswerText(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
                        type="text"
                        className="w-full px-5 py-3.5 rounded-xl bg-steel-blue/20 border border-steel-blue/40 text-papaya-whip text-lg placeholder-papaya-whip/30 focus:outline-none focus:ring-2 focus:ring-steel-blue/60 focus:border-steel-blue/70 transition-all duration-200"
                        placeholder="Type your answer..."
                    />
                    <button
                        disabled={!word[0] || !answerText.trim()}
                        onClick={handleSubmit}
                        className="w-full py-3.5 bg-papaya-whip/10 hover:bg-papaya-whip/20 disabled:opacity-30 disabled:cursor-not-allowed text-papaya-whip border border-papaya-whip/35 hover:border-papaya-whip/60 rounded-xl text-base tracking-widest uppercase transition-all duration-200"
                    >
                        Submit
                    </button>
                </div>
            )}
        </div>
    );
}
