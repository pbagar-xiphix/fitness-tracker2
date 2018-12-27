package com.pluralsight.service.mapper;

import com.pluralsight.domain.*;
import com.pluralsight.service.dto.ActivityDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Activity and its DTO ActivityDTO.
 */
@Mapper(componentModel = "spring", uses = {ExerciseMapper.class})
public interface ActivityMapper extends EntityMapper<ActivityDTO, Activity> {

    @Mapping(source = "exercise.id", target = "exerciseId")
    ActivityDTO toDto(Activity activity);

    @Mapping(source = "exerciseId", target = "exercise")
    Activity toEntity(ActivityDTO activityDTO);

    default Activity fromId(Long id) {
        if (id == null) {
            return null;
        }
        Activity activity = new Activity();
        activity.setId(id);
        return activity;
    }
}
