import { atom } from "jotai";
import type { GameRoomDto } from "../types/GameRoomDto";

export const gameRoomAtom = atom<GameRoomDto>({
    roomCode: "",
    players: [],
});