
import {useEffect, useState } from "react";
import { useAtom, useAtomValue } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { useNextWord } from "../hooks/useNextWord.ts";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp.ts";
import { useGameStateSetSocket } from "../../shared/useGameStateSocket.ts";
import { GameRoomState } from "../../LobbyContainer/types/gameRoomState.ts";
import { EnterCategoryView } from "../components/enterCategoryView.tsx";
import { EnterAnswerView } from "../components/EnterAnswerView.tsx";
import { ResultsView } from "../components/resultsView.tsx";
import { useGameStateGetSocket } from "../../shared/useGameStateGetSocket.ts";
import { useListOfPlayers } from "../../hooks/useListOfPlayers.ts";
import { PlayersList } from "../components/playersListView.tsx";
    
function RoundPage() {
    
    const [timeLeft, setTimeLeft] = useState<number>(30);
    const [player, setPlayer] = useAtom(playerInfoAtom);
      
    const { connected: wordConnected, messages: word, send: sendWord } = useNextWord(player?.roomCode, player?.id);

    const { connected: timerConnected, messages: time, send: sendTime } = useTimerRoundStomp(player?.roomCode, player?.id);

    const { connected: stateConnected, messages: stateResult, send: sendState } = useGameStateSetSocket(player?.roomCode);
    const {connected: stateGetConnected, messages: stateGetResult, send: sendGetState } = useGameStateGetSocket(player?.roomCode);
    const { connected: playersConnected, messages: playersResult, send: sendPlayersLists } = useListOfPlayers(player?.roomCode);


    useEffect(() => {
        if (stateGetConnected) {
            try {
                sendGetState({});
                console.log("Get game state request sent successfully");
            } catch (error) {
                console.error("Error sending get game state request:", error);
            }
        }
       
        console.log("Game state received:", stateResult);
    }, [stateGetConnected, sendGetState]);


    useEffect(() => {
        console.log("Word received:", word);
    }, [word]);

    useEffect(() => {
        sendPlayersLists({});
    }, [sendPlayersLists]);

    useEffect(() => {
        console.log("Time received:", time);
    }, [time]);

   const renderStage = () => {
  switch (stateResult[0]) {
    case GameRoomState.CATEGORY_SELECTION:
      return <EnterCategoryView />;
    case GameRoomState.ANSWERING:
      return (
        <EnterAnswerView
          word={word[0]?.word || ""}
          timeLeft={time[0] ?? 0}
          isImpostor={word[0]?.isImpostor || false}
        />
      );
    case GameRoomState.RESULTS:
      return <ResultsView />;
    default:
      return null;
  }
};

   return (
  <div className="flex min-h-screen bg-linear-to-br from-deep-space-blue via-steel-blue to-papaya-whip/10">
    {/* Lista graczy po lewej */}
    <div className="w-full md:w-80 shrink-0 flex items-center justify-center p-4">
      <PlayersList players={playersResult.flat()} />
    </div>
    {/* Główna część: etap gry */}
    <div className="flex-1 flex items-center justify-center p-4">
      {renderStage()}
    </div>
  </div>
);
    
}
export default RoundPage;