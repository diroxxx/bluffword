import { DotLottieReact } from '@lottiefiles/dotlottie-react';
import {useEffect, useRef} from "react";

type RoomCodeBoxProps = {
    code?: string;
    numberOfPlayers?: number;
    maxNumberOfPlayers?: number;
};

function RoomCode({ code, numberOfPlayers, maxNumberOfPlayers }: RoomCodeBoxProps) {

    // const numberLottieRef = useRef<any>(null);
console.log(numberOfPlayers, maxNumberOfPlayers);
    // useEffect(() => {
    //     if (!numberLottieRef.current) return;
    //
    //     const progress = numberOfPlayers / maxNumberOfPlayers;
    //     const totalFrames = numberLottieRef.current.getDuration(true);
    //     const frame = Math.floor(progress * totalFrames);
    //
    //     numberLottieRef.current.goToAndStop(frame, true);
    // }, [numberOfPlayers, maxNumberOfPlayers]);

    return (
        <div className="mb-6 text-center">
            <h1 className="text-3xl font-bold mb-2">Room Code: {code}</h1>
            <h2 className="text-xl flex flex-col items-center justify-center">
                {/* Animacja: oczekiwanie lub gotowość */}
                <div className="w-20 h-20">
                    <DotLottieReact
                        src={
                            numberOfPlayers === maxNumberOfPlayers
                                ? "/accepted_anim.lottie"
                                : "/loading_anim.lottie"
                        }
                        loop={numberOfPlayers !== maxNumberOfPlayers}
                        autoplay
                    />
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
                    {numberOfPlayers} / {maxNumberOfPlayers} players
                </p>
            </h2>
        </div>
    );
                }

                export default RoomCode;