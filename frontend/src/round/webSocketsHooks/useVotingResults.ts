import { createStompChannelHook } from "../../lib/CustomStomp";
import type { VotingResultDto } from "../types/VotingResultDto";


export const useVotingResults = (roomCode: string, roundNumber: number) => {
    
    return createStompChannelHook<VotingResultDto, {}>({
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/round/${roundNumber}/result`,
        sendDestination: `/app/room/${roomCode}/round/${roundNumber}/voting-results`
    })();
}