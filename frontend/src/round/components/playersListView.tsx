import type { PlayerInfo } from "../../types/PlayerInfo";


export  function PlayersList({ players }: { players: PlayerInfo[] }) {

    return (
    <div className="bg-deep-space-blue/80 rounded-2xl shadow-xl px-8 py-8 flex flex-col items-center gap-6 max-w-md w-full">
      <h2 className="text-2xl font-bold text-papaya-whip tracking-widest mb-2">PLAYERS</h2>
      <div className="w-full flex flex-col gap-3">
        {players.length === 0 && (
          <div className="text-steel-blue/50 text-center py-4 tracking-wide">
            Waiting for players...
          </div>
        )}
        {players.map((player) => (
          <div
            key={player.id}
            className="flex items-center justify-between bg-deep-space-blue/30 border border-steel-blue/20 rounded-lg px-4 py-3"
          >
            <span className="text-papaya-whip/90 tracking-wide text-lg">{player.nickname}</span>
            {player.isHost && (
              <span className="text-brick-red text-xs tracking-wider font-bold ml-2">
                HOST
              </span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}