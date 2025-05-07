import {useParams} from "react-router-dom";
import {useStompClient, useSubscription} from "react-stomp-hooks";
import {listOfPlayers, PlayerInfo, gameReqAtom} from "../Atom.tsx";
import {useAtom} from "jotai/index";
import Countdown, { CountdownRendererFn } from 'react-countdown';
import {useAtomValue} from "jotai";
import {useEffect, useState} from "react";
import axios from "axios";

 export type roundAnswer = {
    nickname: string;
    answer: string;
}


function GameRoundPage() {

    const stompClient = useStompClient();
    const { code } = useParams<{ code: string }>();
    const {roundNumber} = useParams();

    const storedPlayer = sessionStorage.getItem("currentPlayer");
    const player = storedPlayer ? JSON.parse(storedPlayer) : null;

    const [players, setPlayers] = useAtom(listOfPlayers);
    const gameReq = useAtomValue(gameReqAtom);
    const [answer,setAnswer] = useState<string>("");
    const [roundWord, setRoundWord] = useState<string>("");

    const renderer: CountdownRendererFn = ({ minutes, seconds, completed }) => {
        if (completed) {
            return <span className="text-red-400 font-mono text-xl animate-pulse">Time run out!</span>;
        } else {
            return (
                <span className="font-mono text-2xl bg-black px-4 py-2 rounded shadow-inner border border-yellow-400">
                    {String(minutes).padStart(2, "0")}:{String(seconds).padStart(2, "0")}
                </span>
            );
        }
    };

    console.log(roundWord)
    useEffect(() => {

        async function getWord() {
            try {
                const res = await axios.get(
                    `http://localhost:8080/api/round/${code}/${player.nickname}/round/${roundNumber}`
                );
                console.log(res);
                setRoundWord(res.data);
            } catch (e) {
                console.error("Failed to fetch word:", e);
            }
        }

        getWord();
        // if (code && player?.nickname && roundNumber) {
        //
        // }
    }, [code, roundNumber, player?.nickname]);



    function sumbitAnswer() {
        if (!stompClient){
            return
        }

        const answerMes: roundAnswer = {
            answer,
            nickname: player.nickname
        }
        stompClient.publish({
            destination: `/app/${code}/${roundNumber}/answers`,
            body: JSON.stringify({
            answerMes
            })
        });
    }




    useSubscription(`/topic/room/${code}/players`, (message) => {
        const data = JSON.parse(message.body) as PlayerInfo[];
        setPlayers(data);
    });


    console.log(gameReq.timeForRound)
    return (
        <div className="min-h-screen bg-[#0b1120] text-white p-8 font-sans">
            <div className="flex flex-col md:flex-row gap-6 w-full max-w-7xl mx-auto">

                {/* Left column: Player list */}
                <div className="w-full md:w-1/4 bg-gray-800 p-4 rounded-lg shadow h-fit">
                    <h2 className="text-lg font-semibold text-gray-300 mb-4 text-center">Players</h2>
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

                {/* Middle column: Game area */}
                <div className="flex-1 flex flex-col items-center gap-8">

                    {/* Top: Word and timer */}
                    <div className="flex flex-col md:flex-row justify-between items-center w-full gap-6">
                        <div className="bg-[#1e293b] p-6 rounded-xl shadow-md w-full md:w-1/2 text-center">
                            <h2 className="text-xl font-semibold text-gray-300 mb-2">Word:</h2>
                             <div className="text-3xl font-bold text-yellow-400">
                                {!(roundWord.length <=0) ? roundWord : "something went wrong with fetched round word"}
                            </div>

                        </div>
                        <div className="bg-[#1e293b] p-6 rounded-xl shadow-md w-full md:w-1/2 text-center">
                            <h2 className="text-xl font-semibold text-gray-300 mb-2">Time left:</h2>
                            <Countdown date={Date.now() + gameReq.timeForRound * 1000} renderer={renderer} />
                        </div>
                    </div>

                    {/* Input and submit button */}
                    <div className="flex items-center gap-4 w-full max-w-2xl mt-6">
                        <input
                            type="text"
                            onChange={i => setAnswer(i.target.value)}
                            placeholder="Enter your clue..."
                            className="flex-1 bg-[#1e293b] px-4 py-2 rounded-xl shadow-inner text-white focus:outline-none focus:ring-2 focus:ring-cyan-400"
                        />

                        <button
                            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-xl transition"
                            onClick={sumbitAnswer}
                        >
                            Submit
                        </button>
                    </div>

                    {/* Player role info */}
                    <div className="w-full flex flex-col items-center gap-4">
                        <p className="text-lg text-center text-gray-200">
                            Your role:{" "}
                            <span className={`font-semibold ${player?.isImpostor ? "text-red-400" : "text-green-400"}`}>
                            {player?.isImpostor ? "Impostor" : "Real"}
                        </span>
                        </p>
                    </div>

                </div>
            </div>
        </div>
    );


}

export default GameRoundPage;