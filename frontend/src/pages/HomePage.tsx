import { type ChangeEvent, useState } from "react";
import { useNavigate } from "react-router-dom";

function HomePage() {
    const [code, setCode] = useState<string>("");
    const navigate = useNavigate();

    const handleJoin = () => {
        if (!code.trim()) return alert("Enter room code first!");
        navigate("/enter-name", { state: { mode: "JOIN", code } });
    };

    return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 pointer-events-none">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/15 via-deep-space-blue to-brick-red/15 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/8 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 gap-10">

                {/* Title */}
                <div className="text-center space-y-3">
                    <h1 className="text-6xl md:text-8xl font-thin tracking-widest text-papaya-whip/90">
                        BLUFFWORD
                    </h1>
                    <p className="text-steel-blue/60 text-xs tracking-[0.4em] uppercase">
                        One of you is lying
                    </p>
                </div>

                {/* Actions */}
                <div className="w-full max-w-sm flex flex-col gap-3">

                    <button
                        onClick={() => navigate("/enter-name", { state: { mode: "CREATE" } })}
                        className="w-full py-4 bg-brick-red hover:bg-molten-lava text-papaya-whip rounded-xl text-base tracking-widest uppercase font-medium transition-all duration-300 shadow-lg hover:shadow-brick-red/30 hover:-translate-y-0.5 active:translate-y-0"
                    >
                        Create new room
                    </button>

                    <div className="flex items-center gap-3 py-1">
                        <div className="flex-1 h-px bg-steel-blue/20" />
                        <span className="text-steel-blue/40 text-xs tracking-widest uppercase">or join</span>
                        <div className="flex-1 h-px bg-steel-blue/20" />
                    </div>

                    <div className="flex gap-2">
                        <input
                            type="text"
                            placeholder="Room code"
                            value={code}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setCode(e.target.value.toUpperCase())}
                            onKeyDown={(e) => e.key === "Enter" && handleJoin()}
                            className="flex-1 px-5 py-3.5 bg-deep-space-blue border border-steel-blue/35 text-papaya-whip/90 placeholder-steel-blue/40 rounded-xl focus:outline-none focus:border-steel-blue/70 focus:ring-2 focus:ring-steel-blue/20 text-center tracking-widest text-base uppercase transition-all duration-200"
                        />
                        <button
                            onClick={handleJoin}
                            className="px-7 py-3.5 bg-deep-space-blue hover:bg-steel-blue/20 text-papaya-whip/80 hover:text-papaya-whip border border-steel-blue/35 hover:border-steel-blue/60 rounded-xl tracking-widest uppercase text-sm font-medium transition-all duration-200"
                        >
                            Join
                        </button>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default HomePage;
