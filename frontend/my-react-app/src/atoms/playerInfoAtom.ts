import { type PlayerInfo } from "../types/PlayerInfo.ts";
import { atomWithStorage } from "jotai/utils";


export const playerInfoAtom  = atomWithStorage<PlayerInfo | null>("playerInfo", null);