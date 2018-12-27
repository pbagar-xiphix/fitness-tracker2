package com.pluralsight.service.mapper;

import com.pluralsight.domain.*;
import com.pluralsight.service.dto.GoalDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Goal and its DTO GoalDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GoalMapper extends EntityMapper<GoalDTO, Goal> {



    default Goal fromId(Long id) {
        if (id == null) {
            return null;
        }
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }
}
