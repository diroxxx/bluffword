

interface EnterAnswerViewProps {
    word?: string;
    timeLeft?: number;
    isImpostor?: boolean;
}

export function EnterAnswerView({ word, timeLeft, isImpostor }: EnterAnswerViewProps) {
    return (
        <div className="bg-deep-space-blue/90 rounded-3xl shadow-2xl px-12 py-14 flex flex-col items-center gap-12 max-w-2xl w-full">
            <h2 className="text-4xl font-extrabold text-papaya-whip tracking-widest mb-4 drop-shadow-lg">ROUND</h2>
            <div className="flex flex-col items-center gap-8 w-full">
                <div className="w-full text-center">
                    <span className="block text-steel-blue/70 text-lg uppercase tracking-widest mb-3">Your word</span>
                    <span className="text-4xl md:text-6xl font-bold text-papaya-whip bg-steel-blue/30 px-10 py-6 rounded-2xl shadow-inner tracking-widest select-all">
                        {word && word.length > 0 ? word : <span className="text-steel-blue/50">Loading...</span>}
                    </span>
                </div>
                {/* Impostor info */}
                {typeof isImpostor === "boolean" && (
                    <div className="w-full text-center mt-2">
                        <span className={`inline-block px-8 py-4 rounded-2xl text-2xl font-bold tracking-wider shadow-lg
                            ${isImpostor
                                ? "bg-brick-red/40 text-brick-red border-2 border-brick-red/60"
                                : "bg-steel-blue/30 text-steel-blue/90 border-2 border-steel-blue/40"
                            }`
                        }>
                            {isImpostor ? "You are the IMPOSTOR!" : "You are NOT the impostor"}
                        </span>
                    </div>
                )}
                <div className="w-full text-center mt-8">
                    <span className="block text-steel-blue/70 text-lg uppercase tracking-widest mb-2">Time left</span>
                    <span className="text-5xl md:text-7xl font-extrabold text-papaya-whip bg-brick-red/30 px-12 py-6 rounded-2xl shadow-inner tracking-widest">
                        {timeLeft && timeLeft > 0 ? timeLeft : <span className="text-steel-blue/50">Loading...</span>} 
                        {/* <span className="text-2xl text-steel-blue/70">seconds</span> */}
                    </span>
                </div>
            </div>
        </div>
    );
}