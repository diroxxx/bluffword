import { atomWithStorage } from "jotai/utils";
import type { GameRoomDto } from "../types/GameRoomDto";

export const gameRoomAtom = atomWithStorage<GameRoomDto>("gameRoom", {
    roomCode: "",
    players: [],
});