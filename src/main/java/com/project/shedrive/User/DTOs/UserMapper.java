package com.project.shedrive.User.DTOs;

import com.project.shedrive.User.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(RegisterRequest registerRequest);
}
