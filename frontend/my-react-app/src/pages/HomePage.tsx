import { useAtomValue } from "jotai";
import {type ChangeEvent, useState } from "react";
import {useNavigate} from "react-router-dom";
import { playerInfoAtom } from "../atoms/playerInfoAtom";

function HomePage() {
    const [code, setCode] = useState<string>("");
    const navigate = useNavigate();
    const userInfo = useAtomValue(playerInfoAtom);
    const handleCodeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCode(e.target.value.toUpperCase());
    };
    console.log(userInfo);
    return (
        <div className="relative min-h-screen overflow-hidden bg-deep-space-blue">
            <div className="absolute inset-0 opacity-50">
                <div className="absolute inset-0 bg-linear-to-br from-molten-lava/20 via-deep-space-blue to-brick-red/20 animate-pulse-slow" />
                <div className="absolute inset-0 bg-linear-to-tl from-steel-blue/10 to-transparent animate-pulse-slow delay-1000" />
            </div>

            <div className="absolute inset-0 backdrop-blur-sm" />

            <div className="relative min-h-screen flex flex-col items-center justify-center px-6 gap-16">

                <div className="text-center space-y-8">
                    <h1 className="text-6xl md:text-8xl font-thin tracking-widest text-papaya-whip/90">
                        BLUFFWORD
                    </h1>

                    <p className="text-steel-blue/70 text-sm md:text-base tracking-widest uppercase">
                        One of you is lying
                    </p>
                </div>

                <div className="w-full max-w-sm space-y-6">

                    <button
                        onClick={() => navigate("/enter-name", { state: { mode: "CREATE" } })}
                        className="w-full py-4 border border-papaya-whip/30 text-papaya-whip/90 hover:text-papaya-whip hover:border-papaya-whip hover:bg-papaya-whip/5 rounded-xl text-lg tracking-wider transition-all duration-300 backdrop-blur-xl"
                    >
                        Create new room
                    </button>

                    <div className="flex gap-3">
                        <input
                            type="text"
                            placeholder="ROOM CODE"
                            value={code}
                            onChange={handleCodeChange}
                            className="flex-1 px-5 py-4 bg-deep-space-blue/50 border border-steel-blue/30 text-papaya-whip/90 placeholder-steel-blue/50 rounded-xl focus:outline-none focus:border-papaya-whip/70 backdrop-blur-xl text-center tracking-widest text-lg uppercase"
                        />

                        <button
                            onClick={() => {
                                if (!code) return alert("Enter room code first!");
                                navigate("/enter-name", { state: { mode: "JOIN", code } });
                            }}
                            className="px-10 py-4 bg-papaya-whip/10 hover:bg-papaya-whip/20 text-papaya-whip border border-papaya-whip/30 rounded-xl font-medium tracking-wider transition-all duration-300 backdrop-blur-xl"
                        >
                            JOIN
                        </button>
                    </div>
                </div>

            </div>
        </div>
    );
}

export default HomePage;
