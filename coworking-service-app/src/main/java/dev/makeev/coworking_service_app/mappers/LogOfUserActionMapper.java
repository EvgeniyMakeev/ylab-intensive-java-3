package dev.makeev.coworking_service_app.mappers;

import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogOfUserActionMapper {

    @Mapping(target = "localDateTime", source = "logOfUserAction.localDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    LogOfUserActionDTO toLogOfUserActionDTO (LogOfUserAction logOfUserAction);
}
