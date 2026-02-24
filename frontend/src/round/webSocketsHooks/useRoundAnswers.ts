import { createStompChannelHook } from "../../lib/CustomStomp.ts";
import type { RoundAnswer } from "../types/roundAnswer.ts";

export const useRoundAnswers = (roomCode: string, roundNumber: number, playerId: string) => {
  return createStompChannelHook<RoundAnswer[], {}>(  {
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/round/${roundNumber}/answers`,
    sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/${playerId}/answers`,

  })();
};