import { atom } from "jotai";
import {Client} from "react-stomp-hooks";

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

export const connectedToWebSocket = atom<boolean>(false)
export const stompClientState = atom<Client| null>(null);

export const modes = atom<string[]>([]);
