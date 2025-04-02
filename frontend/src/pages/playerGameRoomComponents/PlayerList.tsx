import {usePlayer} from "../../PlayerContext.tsx";


type PlayerInfo = {
    nickname: string;
};

type PlayerListProps = {
    players: PlayerInfo[];
};

function PlayerList({ players}: PlayerListProps) {
    const { player } = usePlayer();

    return (

        <div className="bg-gray-800 p-4 rounded-lg shadow w-full max-w-sm">
            <h3 className="text-lg font-semibold mb-2">Players in lobby:</h3>
            <ul className="list-disc pl-5 space-y-1">
                {players.map((p, i) => (
                    <li
                        key={i}
                        className={`font-medium list-none ${
                            p.nickname === player?.nickname ? "text-green-400" : "text-white"
                        }`}
                    >
                        {p.nickname}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default PlayerList;