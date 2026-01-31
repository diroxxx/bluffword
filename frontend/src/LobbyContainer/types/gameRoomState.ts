export type GameRoomState =
    | "LOBBY"
    | "GAME_START"
    | "ROUND_START"
    | "CATEGORY_SELECTION"
    | "WORD_SELECTION"
    | "VOTING"
    | "ROUND_END"
    | "GAME_END";

export const GameRoomState = {
    LOBBY: "LOBBY",
    GAME_START: "GAME_START",
    CATEGORY_SELECTION: "CATEGORY_SELECTION",
    ROUND_START: "ROUND_START",
    WORD_SELECTION: "WORD_SELECTION",
    VOTING: "VOTING",
    ROUND_END: "ROUND_END",
    GAME_END: "GAME_END"
} as const;