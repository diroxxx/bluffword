import { createStompChannelHook } from "../../lib/CustomStomp";
import type { VoteDto } from "../types/voteDto";

export const useVotes = (roomCode: string, roundNumber: number) => {
    
  return createStompChannelHook<VoteDto[], VoteDto>(  {
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/round/${roundNumber}/votes`,
    sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/vote`
    

    })
    ();
};