
import { useEffect, useState } from "react";
import { createStompChannelHook } from "../../lib/CustomStomp.ts";
import type { PlayerInfo } from "../../types/PlayerInfo.ts";
import { useAtom, useAtomValue } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { useNextWord } from "../hooks/useNextWord.ts";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp.ts";
function RoundPage() {
    
    const [timeLeft, setTimeLeft] = useState<number>(30);
    const userInfo = useAtomValue(playerInfoAtom);
    const { connected, messages: word, send } = useNextWord(userInfo?.roomCode, userInfo?.id);

    const { connected: timerConnected, messages: time, send: sendTime } = useTimerRoundStomp(userInfo?.roomCode);

    useEffect(() => {
       if (!connected) return;
        
        
    }, []);

    return (
    <div>
        
        <p>Time left: {time} seconds</p>

    </div>
    );
    
}
export default RoundPage;