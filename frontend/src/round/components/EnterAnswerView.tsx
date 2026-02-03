

interface EnterAnswerViewProps {
    word?: string;
    timeLeft?: number;
    isImpostor?: boolean;
}
export function EnterAnswerView({ word, timeLeft, isImpostor }: EnterAnswerViewProps) {
    return (
        <div className="flex flex-col items-center gap-5 max-w-2xl w-full">
            {/* Header z timerem - osobne karty */}
            <div className="flex items-center justify-between w-full gap-4">
                <div className="bg-deep-space-blue/95 border-2 border-steel-blue/30 rounded-2xl shadow-xl px-6 py-4 flex-1">
                    <h2 className="text-2xl font-extrabold text-papaya-whip tracking-widest text-center">ROUND</h2>
                </div>
                
                <div className="bg-deep-space-blue/95 border-2 border-brick-red/40 rounded-2xl shadow-xl px-6 py-4">
                    <div className="flex items-center gap-3">
                        <span className="text-papaya-whip/70 text-sm uppercase tracking-wide font-semibold">Time:</span>
                        <span className="text-3xl font-extrabold text-papaya-whip bg-brick-red/40 px-4 py-1 rounded-lg border border-brick-red/50">
                            {timeLeft && timeLeft > 0 ? timeLeft : "..."}
                        </span>
                    </div>
                </div>
            </div>
            <div className={`w-full rounded-2xl shadow-xl px-8 py-6 text-center border-2
                            ${isImpostor
                                ? "bg-brick-red/30 border-brick-red"
                                : "bg-teal-600/30 border-teal-500"
                            }`
                        }>
                            <span className={`text-xl font-bold tracking-wider
                                ${isImpostor ? "text-brick-red drop-shadow-lg" : "text-teal-300 drop-shadow-lg"}`
                            }>
                                {isImpostor ? "You are the IMPOSTOR!" : "You are NOT the impostor"}
                            </span>
                        </div>
            {/* Główne słowo - hero card */}
            <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-10 py-10 text-center">
                <span className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-4 font-semibold">Your word</span>
                <span className="text-5xl md:text-6xl font-bold text-papaya-whip bg-steel-blue/40 px-10 py-6 rounded-2xl shadow-inner tracking-widest select-all inline-block border border-steel-blue/50">
                    {word && word.length > 0 ? word : <span className="text-steel-blue/50">Loading...</span>}
                </span>
            </div>

            

            {/* Input - osobny card */}
            <div className="w-full bg-deep-space-blue/95 border-2 border-steel-blue/40 rounded-2xl shadow-xl px-6 py-6">
                <label className="block text-papaya-whip/60 text-sm uppercase tracking-widest mb-3 font-semibold">Your answer</label>
                <input 
                    type="text" 
                    className="w-full px-6 py-4 rounded-xl bg-steel-blue/30 border-2 border-steel-blue/50 text-papaya-whip text-lg placeholder-papaya-whip/40 focus:outline-none focus:ring-2 focus:ring-papaya-whip focus:border-papaya-whip transition-all duration-300" 
                    placeholder="Type your answer here..." 
                />
            </div>
            <input type="submit" 
                value="SUBMIT ANSWER" 
                className="w-full bg-green-600 hover:bg-green-700 text-papaya-whip border border-papaya-whip/30 rounded-xl text-lg font-medium tracking-wider transition-all duration-300 px-6 py-4 cursor-pointer backdrop-blur-xl"
            />
        </div>
    );
}