import { atom } from "jotai";

export type GameRequest = {
    mode: string;
    numberOfRounds: number;
    maxNumbersOfPlayers: number;
    timeForVoting: number;
    timeForRound: number;
};
export const gameReqAtom = atom<GameRequest>({
    mode: "STATIC_IMPOSTOR",
    numberOfRounds: 5,
    maxNumbersOfPlayers: 6,
    timeForVoting: 30,
    timeForRound: 60,
});

export type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};
export const listOfPlayers = atom<PlayerInfo[]>([])

export const roomCode = atom<string | null>(null)

// export const playerAtom = atomWithStorage<PlayerInfo | null>(
//     "currentPlayer",
//     null,
//     sessionStorage
// );

