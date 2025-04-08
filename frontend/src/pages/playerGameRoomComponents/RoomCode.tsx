import { DotLottieReact } from '@lottiefiles/dotlottie-react';
import {useAtomValue} from "jotai";
import {gameReqAtom, listOfPlayers} from "../../Atom.tsx";
import {useState} from "react";
import CountUp from 'react-countup';


function RoomCode({code}: {code?: string}) {
    const players = useAtomValue(listOfPlayers);
    const gameReq = useAtomValue(gameReqAtom);
    const [revealed, setRevealed] = useState(false);
    const [copied, setCopied] = useState(false);

    const isReady = players.length === gameReq.maxNumbersOfPlayers;
    const animationSrc = isReady ? "/accepted_anim.lottie" : "/loading_anim.lottie";


    const handleReveal = () =>{
        if (revealed) {
            setRevealed(false);
        }else {
            setRevealed(true);
        }
    }

    const handleCopy = async () => {
        try {
            if (code) {
                await navigator.clipboard.writeText(code);
            }
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
        } catch (err) {
            console.error("Copy failed:", err);
        }
    };

    return (
        <div className="mb-6 text-center">
            <h1 className="text-3xl font-bold mb-2">Room Code: </h1>
            <div
                onClick={handleReveal}
                className={`text-3xl font-bold px-6 py-2 rounded-md cursor-pointer transition duration-300
                    ${revealed ? "backdrop-blur-0" : "blur-sm hover:blur-none"} 
                    bg-gray-800 text-white select-none`}
            >
                {code}

            </div>
            {!revealed && (
                <p className="text-sm text-gray-400 mt-1">(Click to reveal)</p>
            )}
            <button
                onClick={handleCopy}
                className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded-md text-sm font-medium transition"
            >
                {copied ? "Copied!" : "Copy"}
            </button>

            <h2 className="text-xl flex flex-col items-center justify-center">
                <div className="w-20 h-20">
                    <DotLottieReact
                        key={animationSrc}
                        src={animationSrc}
                        loop={!isReady}
                        autoplay
                    />

                </div>

                <div className="text-3xl font-mono text-emerald-400 drop-shadow-md">
                    <CountUp
                        end={players.length}
                        duration={0.6}
                    />
                    <span className="text-emerald-200"> / {gameReq.maxNumbersOfPlayers}</span>
                </div>

                {/*<div className="text-sm text-gray-400">*/}
                {/*    {players.length} / {gameReq.maxNumbersOfPlayers}*/}
                {/*</div>*/}
</h2>
</div>
);

}

export default RoomCode;