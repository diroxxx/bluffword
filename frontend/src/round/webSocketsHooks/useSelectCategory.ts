import { createStompChannelHook } from "../../lib/CustomStomp";

export const useSelectCategory = (roomCode: string, playerId: string, roundNumber: number) => {
    return createStompChannelHook<never, string>({
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/round/${roundNumber}/category`,
        sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/${playerId}/category`,
    })();
};