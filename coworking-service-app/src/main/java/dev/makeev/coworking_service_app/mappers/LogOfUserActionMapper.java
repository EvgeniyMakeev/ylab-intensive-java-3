package dev.makeev.coworking_service_app.mappers;

import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogOfUserActionMapper {
    LogOfUserActionMapper INSTANCE = Mappers.getMapper(LogOfUserActionMapper.class);

    @Mapping(target = "localDateTime", source = "logOfUserAction.localDateTime", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "loginOfUser", source = "logOfUserAction.loginOfUser")
    @Mapping(target = "messageAboutAction", source = "logOfUserAction.messageAboutAction")
    LogOfUserActionDTO toLogOfUserActionDTO (LogOfUserAction logOfUserAction);
}
