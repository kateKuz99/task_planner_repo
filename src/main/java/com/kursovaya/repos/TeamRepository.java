package com.kursovaya.repos;

import com.kursovaya.model.Team;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TeamRepository extends R2dbcRepository<Team,Long> {
}
