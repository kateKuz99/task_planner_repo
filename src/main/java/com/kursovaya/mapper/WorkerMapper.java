package com.kursovaya.mapper;

import com.kursovaya.dto.WorkerDto;
import com.kursovaya.model.Worker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkerMapper extends Mappable<Worker, WorkerDto>{
}
