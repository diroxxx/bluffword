export type GameRoomState =
    | "LOBBY"
    | "ROUND_START"
    | "CATEGORY_SELECTION"
    | "ANSWERING"
    | "VOTING"
    | "RESULTS"
    | "GAME_END";

export const GameRoomState = {
    LOBBY: "LOBBY",
    CATEGORY_SELECTION: "CATEGORY_SELECTION",
    ROUND_START: "ROUND_START",
    ANSWERING: "ANSWERING",
    VOTING: "VOTING",
    RESULTS: "RESULTS",
    GAME_END: "GAME_END"
} as const;