import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { useRoundAnswers } from "../webSocketsHooks/useRoundAnswers";
import { useAtom } from "jotai";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import type { RoundAnswer } from "../types/roundAnswer";
import { useEffect } from "react";

interface ResultsViewProps {
    answer: string;
    nickname: string;
}

export function ResultsView({ roundAnswers }: { roundAnswers: ResultsViewProps[] }) {


    const [player, setPlayer] = useAtom(playerInfoAtom);
    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);

    
    useEffect(() => {
        console.log("Round answers received:", roundAnswers);
    }, [roundAnswers]);

    return (
            <div className="flex flex-col items-center gap-8 max-w-2xl w-full">
                <div className="bg-deep-space-blue/95 border-2 border-steel-blue/30 rounded-2xl shadow-xl px-8 py-6 w-full text-center">
                    <h2 className="text-3xl font-extrabold text-papaya-whip tracking-widest mb-2">Round Results</h2>
                    <span className="text-papaya-whip/60 text-sm uppercase tracking-widest font-semibold">See what everyone answered!</span>
                </div>
                <div className="w-full flex flex-col gap-6">
                    {(!roundAnswers || roundAnswers.length === 0) ? (
                        <div className="bg-deep-space-blue/95 border-2  rounded-xl px-6 py-6 text-center text-papaya-whip/60 text-lg">
                            No answers yet...
                        </div>
                    ) : (
                        roundAnswers.map((ans, idx) => (
                            <div
                                key={idx}
                                className={`w-full flex items-center gap-4 bg-deep-space-blue/95 border-2 rounded-2xl shadow-xl px-6 py-4`}
                            >
                                <div className="flex-1 text-left">
                                    <span className={`text-lg font-bold tracking-wide`}>
                                        {ans.nickname || "Unknown"}
                                    </span>
                                    
                                </div>
                                <div className="flex-1 text-center">
                                    <span className="text-papaya-whip text-xl font-semibold">
                                        {ans.answer || <span className="text-steel-blue/50">No answer</span>}
                                    </span>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        );
}