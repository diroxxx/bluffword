import { createStompChannelHook } from "../../lib/CustomStomp";
import type { PlayerWordResponse } from "../types/playerWordResponse";

export const useStartRound = (roomCode?: string, playerId?: string, roundNumber?: number) => {

if (!roomCode) {
    return { connected: false, messages: [], send: () => {} };
  }


      return createStompChannelHook<PlayerWordResponse, {}>(  {
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/player/${playerId}/round/word`,
        sendDestination: `/app/room/${roomCode}/round/player/${playerId}/word`,
        // sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/${playerId}/answers`,
      })();
}