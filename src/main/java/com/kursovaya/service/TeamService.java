package com.kursovaya.service;

import com.kursovaya.model.Team;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TeamService {
    Mono<Team> get(Long id);
    Flux<Team> getAll();
    Mono<Void> delete(Long id);
    Mono<Team> save(Team team);
}
