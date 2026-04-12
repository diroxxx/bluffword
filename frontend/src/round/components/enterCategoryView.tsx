import { useEffect } from "react";
import { useAtom } from "jotai";
import { playerInfoAtom } from "../../atoms/playerInfoAtom";
import { gameRoomAtom } from "../../atoms/gameRoomAtom";
import { useCategoryChoices } from "../webSocketsHooks/useCategoryChoices";
import { useSelectCategory } from "../webSocketsHooks/useSelectCategory";

export function EnterCategoryView() {
    const [player] = useAtom(playerInfoAtom);
    const [gameRoom] = useAtom(gameRoomAtom);

    const roundNumber = gameRoom.currentRound || 1;

    const { connected, messages: choices, send: requestChoices } = useCategoryChoices(
        player?.roomCode || "",
        player?.id || "",
        roundNumber,
    );

    const { send: sendCategory } = useSelectCategory(
        player?.roomCode || "",
        player?.id || "",
        roundNumber,
    );

    useEffect(() => {
        if (connected) requestChoices("");
    }, [connected]);

    const categories = choices[0] ?? [];
    const isChooser = categories.length > 0;

    return (
        <div className="flex flex-col items-center gap-8 max-w-xl w-full">
            <div className="text-center space-y-2">
                <span className="text-steel-blue/60 text-xs tracking-[0.35em] uppercase">Round {roundNumber}</span>
                <h2 className="text-4xl text-papaya-whip tracking-widest">CHOOSE CATEGORY</h2>
            </div>

            {isChooser ? (
                <div className="w-full flex flex-col gap-4">
                    {categories.map((category) => (
                        <button
                            key={category}
                            onClick={() => sendCategory(JSON.stringify({ category }))}
                            className="w-full py-5 bg-deep-space-blue/70 hover:bg-steel-blue/30 border border-steel-blue/30 hover:border-steel-blue/70 text-papaya-whip text-xl tracking-widest rounded-2xl transition-all duration-200"
                        >
                            {category}
                        </button>
                    ))}
                </div>
            ) : (
                <div className="text-papaya-whip/40 text-sm tracking-widest uppercase">
                    Waiting for another player to choose...
                </div>
            )}
        </div>
    );
}