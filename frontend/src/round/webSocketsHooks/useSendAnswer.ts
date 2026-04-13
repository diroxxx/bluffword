import { createStompChannelHook } from "../../lib/CustomStomp";

// subscribeDestination is fixed (word topic) so the connection never resets when roundNumber changes.
// Only sendDestination changes, which updates the useCallback without reconnecting.
export const useSendAnswer = (roomCode: string, playerId: string, roundNumber: number) => {
    return createStompChannelHook<never, string>({
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/player/${playerId}/round/word`,
        sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/${playerId}/answers`,
    })();
};