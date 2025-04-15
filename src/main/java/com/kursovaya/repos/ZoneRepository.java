package com.kursovaya.repos;

import com.kursovaya.dto.ZoneDto;
import com.kursovaya.model.TaskStatus;
import com.kursovaya.model.Zone;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ZoneRepository extends R2dbcRepository<Zone,Long> {
    Flux<Zone> findAllByBoardId(Long boardId);

    @Query("Delete From zones where board_id = :boardId")
    Mono<Void> deleteByBoardId(Long boardId);

    @Query("SELECT * FROM zones WHERE board_id = :boardId AND task_status = :taskStatus")
    Mono<Long> findByBoardIdAndTaskStatus(@Param("boardId") Long boardId, @Param("taskStatus") TaskStatus taskStatus);

}
