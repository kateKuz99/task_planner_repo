package com.kursovaya.mapper;

import com.kursovaya.dto.BoardDto;
import com.kursovaya.model.Board;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardMapper extends Mappable<Board, BoardDto>{
}
