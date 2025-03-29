import { DotLottieReact } from '@lottiefiles/dotlottie-react';

type RoomCodeBoxProps = {
    code?: string;
    connected: boolean;
};

function RoomCode({ code, connected }: RoomCodeBoxProps) {
    return (
        <div className="mb-6 text-center">
            <h1 className="text-3xl font-bold mb-2">Room Code: {code}</h1>
            <h2 className="text-xl flex flex-col items-center justify-center">
                <div className="w-20 h-20">
                    <DotLottieReact
                        src="/loading_anim.lottie"
                        loop
                        autoplay
                    />
                </div>
                {connected ? "Waiting for players..." : "Connecting..."}
            </h2>
        </div>
    );
}
export default RoomCode;