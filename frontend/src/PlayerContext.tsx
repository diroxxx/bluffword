// src/context/PlayerContext.tsx
import React, { createContext, useContext, useState, useEffect } from "react";
import Cookies from "js-cookie";

export type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

type PlayerContextType = {
    player: PlayerInfo | null;
    setPlayer: (player: PlayerInfo) => void;
};

const PlayerContext = createContext<PlayerContextType>({
    player: null,
    setPlayer: () => {},
});

export const PlayerProvider = ({ children }: { children: React.ReactNode }) => {
    const [player, setPlayerState] = useState<PlayerInfo | null>(null);

    // Ustawienie danych w state + cookies
    const setPlayer = (player: PlayerInfo) => {
        setPlayerState(player);
        Cookies.set("nickname", player.nickname);
        Cookies.set("isHost", String(player.isHost));
    };

    // 🔁 Przywracanie danych po odświeżeniu
    useEffect(() => {
        const nickname = Cookies.get("nickname");
        const isHost = Cookies.get("isHost") === "true";

        if (nickname) {
            setPlayerState({
                nickname,
                isHost,
                isImpostor: false, // lub z cookies jeśli chcesz trzymać też impostora
            });
        }
    }, []);

    return (
        <PlayerContext.Provider value={{ player, setPlayer }}>
            {children}
        </PlayerContext.Provider>
    );
};

export const usePlayer = () => useContext(PlayerContext);
