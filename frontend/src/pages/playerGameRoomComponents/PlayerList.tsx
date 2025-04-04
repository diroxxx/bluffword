import {listOfPlayers, PlayerInfo} from "../../Atom.tsx";
import {useAtomValue} from "jotai";
import axios from "axios";

function PlayerList({code}: {code?: string}) {
    const storedPlayerRaw = sessionStorage.getItem("currentPlayer");
    const storedPlayer: PlayerInfo | null = storedPlayerRaw ? JSON.parse(storedPlayerRaw) : null;
    const getListOfPlayers = useAtomValue(listOfPlayers);

    async function  removePlayer(nickname: string)   {
        try{
          const res =  await axios.delete(`http://localhost:8080/api/gameRoom/${code}/players/${nickname}`);
            const { status, message } = res.data;

            if (status === "success") {
                console.log(message);
            } else {
                console.warn(message);
            }
        }catch (err) {
            console.log(err);
        }
    }
     
   

    return (

        <div className="bg-gray-800 p-4 rounded-lg shadow w-full max-w-sm">
            <h3 className="text-lg font-semibold mb-2">Players in lobby:</h3>
            <ul className="space-y-2">
                {getListOfPlayers?.map((p, i) => (
                    <li
                        key={i}
                        className={`flex items-center justify-between font-medium list-none px-2 py-1 rounded 
                        ${p.nickname === storedPlayer?.nickname ? "text-green-400" : "text-white"}`}
                        >
                        <span>{p.nickname}</span>

                        {storedPlayer?.isHost && p.nickname !== storedPlayer?.nickname && (
                            <button onClick={ e => removePlayer(p.nickname)}  className="p-1 hover:opacity-50 transition cursor-pointer">
                                <img src="/delete2.png" alt="Remove" className="w-5 h-5"/>
                            </button>

                        )}
                    </li>
                ))}
            </ul>
        </div>

    ); 
}
export default PlayerList;