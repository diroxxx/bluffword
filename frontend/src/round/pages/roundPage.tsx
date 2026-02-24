
import {useEffect, useState } from "react";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { GameRoomState } from "../../LobbyContainer/types/gameRoomState.ts";
import { EnterCategoryView } from "../components/enterCategoryView.tsx";
import { EnterAnswerView } from "../components/EnterAnswerView.tsx";
import { ResultsView } from "../components/resultsView.tsx";
import { useGameStateGetSocket } from "../../shared/useGameStateGetSocket.ts";
import { useListOfPlayers } from "../../hooks/useListOfPlayers.ts";
import { PlayersList } from "../components/playersListView.tsx";
import { gameRoomAtom } from "../../atoms/gameRoomAtom.ts";
import { useRoundAnswers } from "../webSocketsHooks/useRoundAnswers.ts";
    
function RoundPage() {
    
    const [timeLeft, setTimeLeft] = useState<number>(30);
    const [player, setPlayer] = useAtom(playerInfoAtom);

    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);
      

 if (
        !player ||
        !player.roomCode ||
        !player.id ||
        !gameRoom ||
        !gameRoom.currentRound
    ) {
        return null; // lub spinner
    }
    const {connected: stateGetConnected, messages: stateGetResult, send: sendGetState } = useGameStateGetSocket(player?.roomCode);

    const { connected: playersConnected, messages: playersResult, send: sendPlayersLists } = useListOfPlayers(player?.roomCode);

    const { connected: roundAnswersConnected, messages: roundAnswers, send: sendRoundAnswers } = useRoundAnswers(player?.roomCode, gameRoom.currentRound || 1, player.id);
    


    useEffect(() => {
        if (stateGetConnected) {
            setGameRoom((prev) => ({
                ...prev,
                state: stateGetResult[0] || GameRoomState.ANSWERING
            }));
        }
    }, [setGameRoom, stateGetConnected, stateGetResult]);

    useEffect(() => {
        sendPlayersLists({});
    }, [sendPlayersLists]);

   const renderStage = () => {

  switch (gameRoom.state) {
    case GameRoomState.CATEGORY_SELECTION:
      return <EnterCategoryView />;

    case GameRoomState.ANSWERING:
      return (
        <EnterAnswerView/>
      );
      
    case GameRoomState.RESULTS:
      return <ResultsView 
        roundAnswers={roundAnswers[0] || []}
      />;
    default:
      return null;
  }
};


return (
  <div className="flex min-h-screen bg-linear-to-br from-deep-space-blue via-steel-blue to-papaya-whip/10">
    <div className="w-full md:w-80 lg:w-96 shrink-0 flex items-start justify-center p-6 md:p-8">
      <div className="w-full max-w-xs">
        <PlayersList players={playersResult.flat()} />
      </div>
    </div>
    
    <div className="flex-1 flex items-center p-6 md:p-8">
      <div className="w-full flex justify-center md:-ml-40 lg:-ml-48">
        {renderStage()}
      </div>
    </div>
  </div>
);
    
}
export default RoundPage;