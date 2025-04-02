import {useParams} from "react-router-dom";
import {usePlayer} from "../PlayerContext.tsx";

function GameRoundPage() {
    const { player } = usePlayer();
    const { code } = useParams();

    return (
        <div className="min-h-screen bg-gray-900 text-white p-6 flex flex-col items-center">
            {/* Góra: słowo i licznik */}
            <div className="flex justify-between items-center w-full max-w-4xl mb-6">
                <h2 className="text-2xl font-bold">Słowo: <span className="text-yellow-400">Pizza</span></h2>
                <span className="text-lg bg-gray-800 px-4 py-2 rounded">⏱️ 30s</span>
            </div>

            {/* Środek: pole tekstowe */}
            <textarea
                placeholder="Wpisz swoją definicję..."
                className="w-full max-w-4xl p-4 text-black rounded mb-6"
                rows={4}
            />

            {/* Dół: Rola + przycisk */}
            <div className="w-full max-w-4xl flex flex-col items-center gap-4">
                <p className="text-lg">
                    Twoja rola: <span className="font-semibold text-cyan-400">{player?.isImpostor ? "Impostor" : "Prawdziwy"}</span>
                </p>
                <button className="bg-blue-600 px-6 py-2 rounded hover:bg-blue-700">
                    ✅ Zatwierdź odpowiedź
                </button>
            </div>
        </div>
    );
}
export default GameRoundPage;