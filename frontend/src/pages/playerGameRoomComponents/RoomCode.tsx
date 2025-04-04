import { DotLottieReact } from '@lottiefiles/dotlottie-react';
import {useAtomValue} from "jotai";
import {gameReqAtom, listOfPlayers} from "../../Atom.tsx";
import {useState} from "react";

function RoomCode({code}: {code?: string}) {
    const players = useAtomValue(listOfPlayers);
    const gameReq = useAtomValue(gameReqAtom);
    const [revealed, setRevealed] = useState(false);
    const [copied, setCopied] = useState(false);

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
                        src={"/loading_anim.lottie"}
                        loop={true}
                        autoplay
                    />
                    {/*<DotLottieReact*/}
                    {/*    src={*/}
                    {/*        numberOfPlayers === maxNumberOfPlayers*/}
                    {/*            ? "/accepted_anim.lottie"*/}
                    {/*            : "/loading_anim.lottie"*/}
                    {/*    }*/}
                    {/*    loop={numberOfPlayers !== maxNumberOfPlayers}*/}
                    {/*    autoplay*/}
                    {/*/>*/}
                </div>

                {/* Licznik graczy */}
                {/*<div className="mt-2 w-24 h-24">*/}
                {/*    <DotLottieReact*/}
                {/*        src="/loading_numb_anim.lottie"*/}
                {/*        loop={false}*/}
                {/*        autoplay={false}*/}
                {/*        lottieRef={numberLottieRef}*/}
                {/*    />*/}
                {/*</div>*/}

                <p className="mt-1 text-sm text-gray-300">
                    {players?.length} / {gameReq.maxNumbersOfPlayers} players
                </p>
            </h2>
        </div>
    );

}

export default RoomCode;