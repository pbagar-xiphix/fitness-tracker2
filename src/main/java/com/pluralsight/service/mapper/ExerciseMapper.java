package com.pluralsight.service.mapper;

import com.pluralsight.domain.*;
import com.pluralsight.service.dto.ExerciseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Exercise and its DTO ExerciseDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ExerciseMapper extends EntityMapper<ExerciseDTO, Exercise> {


    @Mapping(target = "activities", ignore = true)
    Exercise toEntity(ExerciseDTO exerciseDTO);

    default Exercise fromId(Long id) {
        if (id == null) {
            return null;
        }
        Exercise exercise = new Exercise();
        exercise.setId(id);
        return exercise;
    }
}
