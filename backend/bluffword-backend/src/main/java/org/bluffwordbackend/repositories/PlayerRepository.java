package org.bluffwordbackend.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.models.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {


    @Query("""
    select   p from Player p
    join RoomPlayer rp on rp.player = p
    where rp.gameRoom.code = :roomCode

""")
    List<Player> findPlayersByRoomCode(@Param("roomCode") String roomCode);




}
