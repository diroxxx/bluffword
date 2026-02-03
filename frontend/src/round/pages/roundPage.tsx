
import {useEffect, useState } from "react";
import { useAtom, useAtomValue } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { useStartRound } from "../hooks/useStartRound.ts";
import { useTimerRoundStomp } from "../hooks/useTimerRoundStomp.ts";
import { GameRoomState } from "../../LobbyContainer/types/gameRoomState.ts";
import { EnterCategoryView } from "../components/enterCategoryView.tsx";
import { EnterAnswerView } from "../components/EnterAnswerView.tsx";
import { ResultsView } from "../components/resultsView.tsx";
import { useGameStateGetSocket } from "../../shared/useGameStateGetSocket.ts";
import { useListOfPlayers } from "../../hooks/useListOfPlayers.ts";
import { PlayersList } from "../components/playersListView.tsx";
import { gameRoomAtom } from "../../atoms/gameRoomAtom.ts";
    
function RoundPage() {
    
    const [timeLeft, setTimeLeft] = useState<number>(30);
    const [player, setPlayer] = useAtom(playerInfoAtom);

    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);
      
    const { connected: wordConnected, messages: word, send: sendAnswer } = useStartRound(player?.roomCode, player?.id);

    const { connected: timerConnected, messages: time, send: sendTime } = useTimerRoundStomp(player?.roomCode, player?.id);

    const {connected: stateGetConnected, messages: stateGetResult, send: sendGetState } = useGameStateGetSocket(player?.roomCode);

    const { connected: playersConnected, messages: playersResult, send: sendPlayersLists } = useListOfPlayers(player?.roomCode);


    useEffect(() => {
        setGameRoom( prev => ({
            ...prev,
            players: playersResult[0] || [],
        }));
    }, [playersResult]);

    useEffect(() => {
        if (stateGetConnected) {
            setGameRoom((prev) => ({
                ...prev,
                state: stateGetResult[0] || GameRoomState.ANSWERING
            }));
        }
    }, [stateGetConnected, stateGetResult]);


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
    console.log("Current game state:", gameRoom.state);
    console.log("Word for this round:", word);
    console.log("isImpostor:", word[0]?.isImpostor);
  switch (gameRoom.state) {
    case GameRoomState.CATEGORY_SELECTION:
      return <EnterCategoryView />;
    case GameRoomState.ANSWERING:
      return (
        <EnterAnswerView
          word={word[0]?.word || ""}
          timeLeft={time[0] ?? 0}
          isImpostor={word[0]?.isImpostor}
        />
      );
    case GameRoomState.RESULTS:
      return <ResultsView />;
    default:
      return null;
  }
};

return (
  <div className="flex min-h-screen bg-gradient-to-br from-deep-space-blue via-steel-blue to-papaya-whip/10">
    {/* Lista graczy po lewej */}
    <div className="w-full md:w-80 lg:w-96 shrink-0 flex items-start justify-center p-6 md:p-8">
      <div className="w-full max-w-xs">
        <PlayersList players={playersResult.flat()} />
      </div>
    </div>
    
    {/* Główna część: etap gry - kompensacja lewego marginesu */}
    <div className="flex-1 flex items-center p-6 md:p-8">
      <div className="w-full flex justify-center md:-ml-40 lg:-ml-48">
        {renderStage()}
      </div>
    </div>
  </div>
);
    
}
export default RoundPage;