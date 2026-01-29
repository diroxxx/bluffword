package org.bluffwordbackend.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.models.WordCategory;
import org.bluffwordbackend.models.WordPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {


    @Query("""
        SELECT gr FROM GameRoom gr 
        left join fetch gr.players rp
        left join fetch rp.player p
        WHERE gr.code = :code

""")
    Optional<GameRoom> findByCodeFetchPlayers(@Param("code") String code);


    @Query("""
        select case when count(r) > 0 then true else false end from GameRoom gr
        join Round r on r.gameRoom = gr
        join WordPair wp on wp = r.wordPair
        where gr.id = :gameRoomId and wp.realWord = :realWord and wp.impostorWord = :impostorWord
""")
    boolean isWordPairUsed(@Param("realWord") String realWord,
                           @Param("impostorWord") String impostorWord,
                           @Param("gameRoomId") Long gameRoomId);


    List<GameRoom> searchGameRoomByCode(String code);

    List<GameRoom> findDistinctFirstByCode(String code);

    Optional<GameRoom> findGameRoomByCode(String code);

    @Query("""
    select wp from WordPair wp 
    where wp.category = :category 
    and wp not in (
        select r.wordPair from Round r 
        where r.gameRoom.id = :gameRoomId
    )
""")
    List<WordPair> findAllWordPairsUnused(@Param("gameRoomId") Long gameRoomId, @Param("category") WordCategory category);

    @Query("""
    select rp.player from RoomPlayer rp 
    where rp.gameRoom.id = :gameRoomId
""")
    List<Player> findAllPlayersInRoom(@Param("gameRoomId") Long gameRoomId);

}