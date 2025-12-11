package org.bluffwordbackend.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.bluffwordbackend.models.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {


    @Query("""
        SELECT gr FROM GameRoom gr 
        left join fetch gr.players rp
        left join fetch rp.player p
        WHERE gr.code = :code

""")
    Optional<GameRoom> findByCodeFetchPlayers (@Param("code") String code);

}
