
import { use, useEffect, useState } from "react";
import { createStompChannelHook } from "../../lib/CustomStomp.ts";
import type { PlayerInfo } from "../../types/PlayerInfo.ts";
import { useAtom, useAtomValue } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { useNextWord } from "../hooks/useNextWord.ts";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp.ts";
import { useGameStateSocket } from "../../shared/useGameStateSocket.ts";
import { GameRoomState } from "../../LobbyContainer/types/gameRoomState.ts";
import { EnterCategoryView } from "../components/enterCategoryView.tsx";
import { EnterAnswerView } from "../components/EnterAnswerView.tsx";
import { ResultsView } from "../components/resultsView.tsx";
function RoundPage() {
    
    const [timeLeft, setTimeLeft] = useState<number>(30);
    const userInfo = useAtomValue(playerInfoAtom);  
    const { connected, messages: word, send } = useNextWord(userInfo?.roomCode, userInfo?.id);

    const { connected: timerConnected, messages: time, send: sendTime } = useTimerRoundStomp(userInfo?.roomCode, userInfo?.id);

    const { connected: stateConnected, messages: stateResult, send: sendState } = useGameStateSocket(userInfo?.roomCode);
    


    useEffect(() => {
        console.log("Subscribing to:", `/topic/room/${userInfo?.roomCode}/player/${userInfo?.id}/round/word`);
       if (!stateConnected) return;
        if ( stateResult[0] === GameRoomState.ANSWERING) {

        }



    }, [stateConnected, stateResult]);


    useEffect(() => {
        console.log("Word received:", word);
    }, [word]);

    useEffect(() => {
        console.log("Time received:", time);
    }, [time]);

    const renderStage = () => {
    switch (stateResult[0]) {
        case GameRoomState.CATEGORY_SELECTION:
        return <EnterCategoryView />;
        case GameRoomState.ANSWERING:
        return <EnterAnswerView word={word[0].word} timeLeft={time[0]} isImportant={word[0].isImposter} />;
        case GameRoomState.RESULTS:
        return <ResultsView />;
        default:
        return null;
    }
    };

    return (
    <div>
        
        {renderStage()}
    </div>
    );
    
}
export default RoundPage;