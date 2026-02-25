import type { PlayerWordResponse } from "../types/playerWordResponse";
import { createStompChannelHook } from "../../lib/CustomStomp.ts";

export const useRoundWord = (roomCode: string, playerId: number) => {

  return createStompChannelHook<PlayerWordResponse, {}>(  {
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/player/${playerId}/round/word`,
    sendDestination: `/app/room/${roomCode}/round/player/${playerId}/word`,

  })();
};