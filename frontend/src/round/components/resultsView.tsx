import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { useAtom } from "jotai";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import { useEffect, useState } from "react";
import type { RoundAnswer } from "../types/roundAnswer";
import { useVotes } from "../webSocketsHooks/useVotes";
import type { VoteDto } from "../types/voteDto";

export function ResultsView({ roundAnswers }: { roundAnswers: RoundAnswer[] }) {
    const [player] = useAtom(playerInfoAtom);
    const [gameRoom] = useAtom(gameRoomAtom);

    const { connected: votesConnected, messages: votesMessage, send: sendVote } = useVotes(player?.roomCode!, gameRoom.currentRound!);

    const [allVotes, setAllVotes] = useState<VoteDto[]>([]);



    useEffect(() => {
        console.log("Votes received:", votesMessage);
        setAllVotes(votesMessage.flat());
    }, [votesMessage]);

    
    useEffect(() => {
        console.log("Round answers received:", roundAnswers);

    }, [roundAnswers]);


    return (
        <div className="flex flex-col items-center gap-6 max-w-2xl w-full">

            {/* Header */}
            <div className="w-full text-center">
                <h2 className="text-4xl text-papaya-whip tracking-widest">
                    RESULTS
                </h2>
                <div className="flex items-center gap-3 mt-3">
                    <div className="flex-1 h-px bg-steel-blue/25" />
                    <span className="text-papaya-whip/60 text-xs tracking-widest uppercase">
                        Round {gameRoom?.currentRound}
                    </span>
                    <div className="flex-1 h-px bg-steel-blue/25" />
                </div>
            </div>

            {/* Answer cards */}
            <div className="w-full grid grid-cols-2 gap-3">
                {(!roundAnswers || roundAnswers.length === 0) ? (
                    <div className="col-span-2 bg-deep-space-blue/80 border border-steel-blue/15 rounded-2xl px-6 py-10 text-center text-papaya-whip/40 tracking-wider text-sm">
                        No answers submitted yet...
                    </div>
                ) : (
                    roundAnswers.map((ans, idx) => {
                        const isMe = ans.playerId === player?.id;
                       
                        const votesCount = allVotes.filter(v => v.targetId === ans.playerId).length;
                        return (
                            <div
                                key={idx}
                                className={`relative flex flex-col items-center justify-center rounded-2xl p-5 min-h-28 border cursor-pointer transition-all duration-200  hover:shadow-lg
                                    ${isMe
                                        ? "bg-answers border-answers/40 hover:bg-answers-dark hover:border-answers/60 hover:shadow-answers/20"
                                        : "bg-deep-space-blue/70 border-steel-blue/10 hover:bg-deep-space-blue/90 hover:border-steel-blue/30 hover:shadow-steel-blue/10"
                                    }`}

                                    onClick={() => {

                                        if ( isMe ) return; // Can't vote for yourself

                                        sendVote({
                                            voterId: player?.id!,
                                            targetId: ans.playerId,
                                        });
                                    }
                                }
                            >
                                {/* You indicator */}
                                {isMe && (
                                    <div className="absolute top-3 right-4 w-1.5 h-1.5 rounded-full bg-brick-red/60" />
                                )}

                                {/* Votes count */}

                               {votesCount > 0 && (
                                    <div className="absolute top-3 left-4 flex gap-0.5">
                                        {Array.from({ length: votesCount }).map((_, i) => (
                                            <img key={i} src="/anonymousVoteIcon.png" alt="" className="w-4 h-4" />
                                        ))}
                                    </div>
                                )}

                                
                                {/* Answer */}
                                {ans.answer
                                    ? <span className={`text-xl tracking-wide text-center wrap-break-word ${isMe ? "text-papaya-whip" : "text-papaya-whip/75"}`}>
                                        {ans.answer}
                                      </span>
                                    : <span className="text-steel-blue/25 text-sm italic">no answer</span>
                                }
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
}
