

type PlayerInfo = {
    nickname: string;
};

type PlayerListProps = {
    players: PlayerInfo[];
};

function PlayerList({ players}: PlayerListProps) {

    return (

        <div className="bg-gray-800 p-4 rounded-lg shadow w-full max-w-sm">
            <h3 className="text-lg font-semibold mb-2">Players in lobby:</h3>
            <ul className="list-disc pl-5 space-y-1">
                {players.length === 0 ? (
                    <li>No players yet</li>
                ) : (
                    players.map((p, i) => <li key={i}>{p.nickname}</li>)
                )}
            </ul>
        </div>
    );
}

export default PlayerList;