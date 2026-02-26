const VOTE_DURATION = 30;
const RADIUS = 20;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

interface TimerRingProps {
    timeLeft: number;
}

export function TimerRing({ timeLeft }: TimerRingProps) {
    const isUrgent = timeLeft <= 10;
    const progress = timeLeft / VOTE_DURATION;

    return (
        <div className="flex items-center gap-3">
            <div className="relative w-12 h-12 shrink-0">
                <svg className="w-12 h-12 -rotate-90" viewBox="0 0 48 48">
                    <circle cx="24" cy="24" r={RADIUS} fill="none" stroke="rgba(102,155,188,0.1)" strokeWidth="3" />
                    <circle
                        cx="24" cy="24" r={RADIUS}
                        fill="none"
                        stroke={isUrgent ? "#c1121f" : "#fdf0d5"}
                        strokeWidth="3"
                        strokeLinecap="round"
                        strokeDasharray={CIRCUMFERENCE}
                        strokeDashoffset={CIRCUMFERENCE * (1 - progress)}
                        style={{ transition: 'stroke-dashoffset 0.9s linear, stroke 0.4s ease' }}
                    />
                </svg>
                <div className="absolute inset-0 flex items-center justify-center">
                    <span className={`text-xs font-bold tabular-nums transition-colors duration-300 ${isUrgent ? 'text-brick-red' : 'text-papaya-whip/80'}`}>
                        {timeLeft}
                    </span>
                </div>
            </div>
            <span className={`text-xs tracking-widest uppercase transition-colors duration-300 ${isUrgent ? 'text-brick-red/70' : 'text-papaya-whip/40'}`}>
                {isUrgent ? "Hurry up!" : "Vote now"}
            </span>
        </div>
    );
}
