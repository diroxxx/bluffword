import { useAtom } from "jotai";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { useVotingResults } from "../webSocketsHooks/useVotingResults";
import { use, useEffect, useState } from "react";
import { GameEndView } from "./GameEndView";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp";
import { TimerRing } from "./TimerRing";
// 0 → "ROUND X RESULTS"
// 1 → fade out header
// 2 → nickname reveal
// 3 → impostor verdict
type Stage = 0 | 1 | 2 | 3;

export function VotingResultsView() {
    const [player] = useAtom(playerInfoAtom);
    const [gameRoom] = useAtom(gameRoomAtom);

    const { connected: resultConnected, messages: votingResult, send: sendResult } = useVotingResults(player?.roomCode || "", gameRoom.currentRound || 1);
    const { messages: time } = useTimerRoundStomp(player?.roomCode || "", "NEXT_ROUND");

    const [stage, setStage] = useState<Stage>(0);
    const [showGameEnd, setShowGameEnd] = useState(false);
    const [timeLeft, setTimeLeft] = useState(30);

    useEffect(() => {
        console.log("stage:", stage);
        console.log("votingResult:", votingResult);
        console.log("time:", time);
        console.log("gameRoom:", gameRoom);
    }, [stage, votingResult, time, gameRoom]);

    useEffect(() => {
        if (resultConnected) sendResult({});
    }, [resultConnected]);

    useEffect(() => {
        const latest = time.at(-1);
        if (latest !== undefined) setTimeLeft(latest);
    }, [time]);

    useEffect(() => {
        if (votingResult.length === 0) return;

        const t1 = setTimeout(() => setStage(1), 2000);
        const t2 = setTimeout(() => setStage(2), 2800);
        const t3 = setTimeout(() => setStage(3), 4200);
        const t4 = votingResult.some(p => p.isGameOver) ? setTimeout(() => setShowGameEnd(true), 6500) : null;

        return () => { clearTimeout(t1); clearTimeout(t2); clearTimeout(t3);  if (t4) clearTimeout(t4);  };
    }, [votingResult.length]);

    const eliminated = votingResult.find(p => p.isImpostor) ?? votingResult[0];
    const maxVotes = votingResult[0]?.voteCount || 1;
    const noVotes = !eliminated?.nickname;

    if (showGameEnd) return <GameEndView />;

    return (
        <div className="flex flex-col items-center justify-center gap-8 max-w-2xl w-full min-h-64">

            {/* Stage 0–1: "ROUND X RESULTS" */}
            {stage <= 1 && (
                <div className={`flex flex-col items-center gap-2 text-center transition-opacity duration-700 ${stage === 1 ? "opacity-0" : "opacity-100"}`}>
                    <span className="text-steel-blue/50 text-sm tracking-[0.35em] uppercase">
                        Round {gameRoom?.currentRound}
                    </span>
                    <h1 className="text-6xl text-papaya-whip tracking-widest">
                        RESULTS
                    </h1>
                    <div className="flex items-center gap-3 mt-2 w-64">
                        <div className="flex-1 h-px bg-steel-blue/25" />
                        <div className="w-1.5 h-1.5 rounded-full bg-brick-red/50" />
                        <div className="flex-1 h-px bg-steel-blue/25" />
                    </div>
                </div>
            )}

            {/* Stage 2+: nickname */}
            {stage >= 2 && (
                <div className="flex flex-col items-center gap-3 text-center fade-slide-up">
                    {noVotes ? (
                        <span className="text-steel-blue text-xs tracking-[0.3em] uppercase">
                            No one was voted out
                        </span>
                    ) : (
                        <>
                            <span className="text-steel-blue text-xs tracking-[0.3em] uppercase">
                                Most votes received
                            </span>
                            <h2 className="text-5xl text-papaya-whip tracking-wide">
                                {eliminated.nickname}
                            </h2>
                        </>
                    )}
                </div>
            )}

            {/* Stage 3+: timer + verdict */}
            {stage >= 3 && <TimerRing timeLeft={timeLeft} />}

            {stage >= 3 && (
                <div className="flex flex-col items-center gap-1 fade-slide-up">
                    {noVotes ? (
                        <>
                            <span className="text-2xl text-papaya-whip/60 tracking-widest uppercase">
                                No votes cast
                            </span>
                            <span className="text-steel-blue/40 text-xs tracking-widest mt-1">
                                The impostor survives this round...
                            </span>
                        </>
                    ) : eliminated?.isImpostor ? (
                        <>
                            <span className="text-2xl text-brick-red tracking-widest uppercase font-bold">
                                Is the Impostor!
                            </span>
                            <span className="text-steel-blue/40 text-xs tracking-widest mt-1">
                                The team wins this round
                            </span>
                        </>
                    ) : (
                        <>
                            <span className="text-2xl text-papaya-whip/60 tracking-widest uppercase">
                                Is Innocent
                            </span>
                            <span className="text-brick-red/60 text-xs tracking-widest mt-1">
                                The impostor is still out there...
                            </span>
                        </>
                    )}
                </div>
            )}

        </div>
    );
}
