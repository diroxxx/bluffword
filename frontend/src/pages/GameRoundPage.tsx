import {useParams} from "react-router-dom";
import {usePlayer} from "../PlayerContext.tsx";
import {useSubscription} from "react-stomp-hooks";
import {listOfPlayers, PlayerInfo, gameReqAtom} from "../Atom.tsx";
import {useAtom} from "jotai/index";
import Countdown, { CountdownRendererFn } from 'react-countdown';
import PlayerList from "./playerGameRoomComponents/PlayerList.tsx";
import {useAtomValue} from "jotai";

 export type roundAnswer = {
    nickname: number;
    answer: string;
    round: number;
}


function GameRoundPage() {
    const { code } = useParams<{ code: string }>();

    const storedPlayer = sessionStorage.getItem("currentPlayer");
    const player = storedPlayer ? JSON.parse(storedPlayer) : null;
    const [players, setPlayers] = useAtom(listOfPlayers);
    const gameReq = useAtomValue(gameReqAtom);
    const renderer: CountdownRendererFn = ({ minutes, seconds, completed }) => {
        if (completed) {
            return <span className="text-red-400 font-mono text-xl animate-pulse">Czas minął!</span>;
        } else {
            return (
                <span className="font-mono text-2xl bg-black px-4 py-2 rounded shadow-inner border border-yellow-400">
                    {String(minutes).padStart(2, "0")}:{String(seconds).padStart(2, "0")}
                </span>
            );
        }
    };

    useSubscription(`/topic/room/${code}/players`, (message) => {
        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);
    });
    console.log(gameReq.timeForRound)
    return (
        <div className="min-h-screen bg-[#0b1120] text-white p-8 font-sans">
            <div className="flex flex-col md:flex-row gap-6 w-full max-w-7xl mx-auto">
                {/* Lewa kolumna: Lista graczy */}
                <div className="w-full md:w-1/4 bg-gray-800 p-4 rounded-lg shadow h-fit">
                    <h2 className="text-lg font-semibold text-gray-300 mb-4 text-center">Gracze</h2>
                    <ul className="space-y-2">
                        {players?.map((p, i) => (
                            <li
                                key={i}
                                className={`flex items-center justify-between font-medium list-none px-2 py-1 rounded 
                            ${p.nickname === player?.nickname ? "text-green-400" : "text-white"}`}
                            >
                                <span>{p.nickname}</span>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Środkowa kolumna: Gra */}
                <div className="flex-1 flex flex-col items-center gap-8">
                    {/* Top: Słowo i licznik */}
                    <div className="flex flex-col md:flex-row justify-between items-center w-full gap-6">
                        <div className="bg-[#1e293b] p-6 rounded-xl shadow-md w-full md:w-1/2 text-center">
                            <h2 className="text-xl font-semibold text-gray-300 mb-2">Słowo:</h2>
                            <div className="text-3xl font-bold text-yellow-400">Pizza</div>
                        </div>
                        <div className="bg-[#1e293b] p-6 rounded-xl shadow-md w-full md:w-1/2 text-center">
                            <h2 className="text-xl font-semibold text-gray-300 mb-2">Pozostały czas:</h2>
                            <Countdown date={Date.now() + gameReq.timeForRound * 1000} renderer={renderer} />
                        </div>
                    </div>

                    {/* Textarea */}
                    <textarea
                        placeholder="Wpisz swoją definicję..."
                        className="bg-[#1e293b] w-full p-5 rounded-xl shadow-inner focus:outline-none focus:ring-2 focus:ring-cyan-400"
                        rows={4}
                    />

                    {/* Rola + przycisk */}
                    <div className="w-full flex flex-col items-center gap-4">
                        <p className="text-lg text-center text-gray-200">
                            Twoja rola:{" "}
                            <span className={`font-semibold ${player?.isImpostor ? "text-red-400" : "text-green-400"}`}>
                            {player?.isImpostor ? "Impostor" : "Prawdziwy"}
                        </span>
                        </p>
                        <button className="bg-blue-600 px-8 py-3 rounded-lg hover:bg-blue-700 transition duration-200 shadow-md">
                            ✅ Zatwierdź odpowiedź
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );

}

export default GameRoundPage;