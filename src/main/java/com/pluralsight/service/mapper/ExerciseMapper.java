package com.pluralsight.service.mapper;

import com.pluralsight.domain.*;
import com.pluralsight.service.dto.ExerciseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Exercise and its DTO ExerciseDTO.
 */
@Mapper(componentModel = "spring", uses = {ActivityMapper.class})
public interface ExerciseMapper extends EntityMapper<ExerciseDTO, Exercise> {

    @Mapping(source = "activity.id", target = "activityId")
    ExerciseDTO toDto(Exercise exercise);

    @Mapping(source = "activityId", target = "activity")
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
