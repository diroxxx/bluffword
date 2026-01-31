export type GameSettings = {
     roomCode: string;
     roundTotal: number;
     maxPlayers: number;   
     minPlayers: number;
     timeLimitAnswer: number;
     timeLimitVote: number;
     gameMode: GameMode;
     categorySelectionMode: CategorySelectionMode;
     staticCategory?: string;

};


export const GameMode = {
    STATIC_IMPOSTOR: "STATIC_IMPOSTOR",
    ROUND_IMPOSTOR: "ROUND_IMPOSTOR",
} as const;

export type GameMode = typeof GameMode[keyof typeof GameMode];


export const CategorySelectionMode = {
     FIXED: "FIXED",
    RANDOM_PER_ROUND: "RANDOM_PER_ROUND",
    PLAYER_CHOSEN_PER_ROUND: "PLAYER_CHOSEN_PER_ROUND",
} as const;
export type CategorySelectionMode = typeof CategorySelectionMode[keyof typeof CategorySelectionMode];