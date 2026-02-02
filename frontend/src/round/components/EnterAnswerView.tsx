

interface EnterAnswerViewProps {
    word?: string;
    timeLeft?: number;
    isImportant?: boolean;
}

export function EnterAnswerView({ word, timeLeft, isImportant }: EnterAnswerViewProps) {
    return (
            <div>
                <div className="flex flex-col items-center justify-center min-h-screen bg-linear-to-br from-deep-space-blue via-steel-blue to-papaya-whip/10">
                    <div className="bg-deep-space-blue/80 rounded-2xl shadow-xl px-8 py-10 flex flex-col items-center gap-8 max-w-md w-full mt-16">
                        <h2 className="text-3xl font-bold text-papaya-whip tracking-widest mb-2">ROUND</h2>
                        <div className="flex flex-col items-center gap-4 w-full">
                            <div className="w-full text-center">
                                <span className="block text-steel-blue/70 text-xs uppercase tracking-widest mb-1">Your word</span>
                                <span className="text-2xl md:text-4xl font-semibold text-papaya-whip bg-steel-blue/20 px-6 py-3 rounded-xl shadow-inner tracking-widest">
                                    {word && word.length > 0 ? word : <span className="text-steel-blue/50">Loading...</span>}
                                </span>
                            </div>
                            {/* Impostor info */}
                            {typeof isImportant === "boolean" && (
                                <div className="w-full text-center mt-2">
                                    <span className={`inline-block px-4 py-2 rounded-lg text-base font-semibold tracking-wider
                                        ${isImportant
                                            ? "bg-brick-red/30 text-brick-red border border-brick-red/50"
                                            : "bg-steel-blue/20 text-steel-blue/90 border border-steel-blue/40"
                                        }`
                                    }>
                                        {isImportant ? "You are the IMPOSTOR!" : "You are NOT the impostor"}
                                    </span>
                                </div>
                            )}
                            <div className="w-full text-center mt-6">
                                <span className="block text-steel-blue/70 text-xs uppercase tracking-widest mb-1">Time left</span>
                                <span className="text-3xl md:text-5xl font-bold text-papaya-whip bg-brick-red/20 px-8 py-4 rounded-xl shadow-inner tracking-widest">
                                    {timeLeft && timeLeft > 0 ? timeLeft : <span className="text-steel-blue/50">Loading...</span>} <span className="text-base text-steel-blue/70">seconds</span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
}