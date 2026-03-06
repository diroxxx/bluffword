export function GameEndView() {
    return (
        <div className="flex flex-col items-center justify-center gap-6 text-center">
            <p className="text-steel-blue/60 text-xs tracking-[0.35em] uppercase">
                The game is over
            </p>
            <h1 className="text-7xl text-papaya-whip tracking-widest">
                GAME OVER
            </h1>
            <div className="flex items-center gap-3 w-72">
                <div className="flex-1 h-px bg-steel-blue/25" />
                <div className="w-1.5 h-1.5 rounded-full bg-brick-red/50" />
                <div className="flex-1 h-px bg-steel-blue/25" />
            </div>
        </div>
    );
}
