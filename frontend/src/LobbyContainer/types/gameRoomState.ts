export type GameRoomState =
    | "LOBBY"
    | "CATEGORY_SELECTION"
    | "ANSWERING"
    | "VOTING"
    | "VOTING_RESULTS"
    | "GAME_END";

export const GameRoomState = {
    LOBBY: "LOBBY",
    CATEGORY_SELECTION: "CATEGORY_SELECTION",
    ANSWERING: "ANSWERING",
    VOTING: "VOTING",
    VOTING_RESULTS: "VOTING_RESULTS",
    GAME_END: "GAME_END"
} as const;