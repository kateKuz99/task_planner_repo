package com.kursovaya.service;

import com.kursovaya.model.Team;
import com.kursovaya.repos.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;

    @Override
    public Mono<Team> get(Long id) {
        return teamRepository.findById(id);
    }

    @Override
    public Flux<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    public Mono<Void> delete(Long id) {
        return teamRepository.deleteById(id);
    }

    @Override
    public Mono<Team> save(Team team) {
        return teamRepository.save(team);
    }
}
