import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom.ts";
import { GameRoomState } from "../../LobbyContainer/types/gameRoomState.ts";
import { EnterCategoryView } from "../components/enterCategoryView.tsx";
import { EnterAnswerView } from "../components/EnterAnswerView.tsx";
import { ResultsView } from "../components/resultsView.tsx";
import { VotingResultsView } from "../components/VotingResultsView.tsx";
import { GameEndView } from "../components/GameEndView.tsx";
import { useListOfPlayers } from "../../hooks/useListOfPlayers.ts";
import { PlayersList } from "../components/playersListView.tsx";
import { gameRoomAtom } from "../../atoms/gameRoomAtom.ts";
import { useRoundAnswers } from "../webSocketsHooks/useRoundAnswers.ts";
import { useGameStateGetSocket } from "../../shared/useGameStateGetSocket.ts";

function RoundPage() {
    const location = useLocation();
    const [player] = useAtom(playerInfoAtom);
    const [gameRoom, setGameRoom] = useAtom(gameRoomAtom);

    useEffect(() => {
        const initialState = location.state?.initialGameState;
        if (initialState) {
            setGameRoom(prev => ({ ...prev, state: initialState }));
        }
    }, []);

    const { messages: stateMessages } = useGameStateGetSocket(player?.roomCode || "");
    const { messages: playersResult, send: sendPlayersLists } = useListOfPlayers(player?.roomCode || "");
    const { messages: roundAnswers } = useRoundAnswers(player?.roomCode || "", gameRoom.currentRound || 1, player?.id || "");

    useEffect(() => {
        const latest = stateMessages.at(-1);
        if (latest) {
            setGameRoom(prev => ({ ...prev, state: latest }));
        }
    }, [stateMessages]);

    useEffect(() => {
        sendPlayersLists({});
    }, [sendPlayersLists]);

    if (!player || !player.roomCode || !player.id) {
        return null;
    }

    const renderStage = () => {
        switch (gameRoom.state) {
            case GameRoomState.CATEGORY_SELECTION:
                return <EnterCategoryView />;
            case GameRoomState.ANSWERING:
                return <EnterAnswerView />;
            case GameRoomState.VOTING:
                return <ResultsView roundAnswers={roundAnswers[0] || []} />;
            case GameRoomState.VOTING_RESULTS:
                return <VotingResultsView />;
            case GameRoomState.GAME_END:
                return <GameEndView />;
            default:
                return null;
        }
    };

    return (
        <div className="flex flex-col md:flex-row min-h-screen bg-linear-to-br from-deep-space-blue via-steel-blue to-papaya-whip/10">
            <div className="flex md:w-72 lg:w-96 shrink-0 items-start justify-center p-4 md:p-8">
                <div className="w-full max-w-xs">
                    <PlayersList players={playersResult.flat()} />
                </div>
            </div>

            <div className="flex-1 min-w-0 flex items-center justify-center p-4 md:p-8">
                {renderStage()}
            </div>

            <div className="hidden md:block md:w-72 lg:w-96 shrink-0" />
        </div>
    );
}

export default RoundPage;
