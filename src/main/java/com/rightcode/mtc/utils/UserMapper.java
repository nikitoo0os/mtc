package com.rightcode.mtc.utils;

import com.rightcode.mtc.dto.user.UserInfoResponse;
import com.rightcode.mtc.dto.user.UserResponse;
import com.rightcode.mtc.store.entities.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public UserResponse toDto(User entity) {
        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setSurname(entity.getSurname());
        dto.setName(entity.getName());
        if (entity.getPatronymic() != null) {
            dto.setPatronymic(entity.getPatronymic());
        }
        dto.setPhone(entity.getPhoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setDob(entity.getDob().toString());

        if (entity.getPosition() != null) {
            dto.setPosition(entity.getPosition().getId());
        }

        if (entity.getOrganization() != null) {
            dto.setOrganization(entity.getOrganization().getId());
        }

        if (entity.getSpeciality() != null) {
            dto.setSpeciality(entity.getSpeciality().getId());
        }

        return dto;
    }

    public UserInfoResponse toInfoDto(User user) {
        UserInfoResponse dto = new UserInfoResponse();
        dto.setId(user.getId());
        dto.setFullName(String.format("%s %s %s", user.getSurname(), user.getName(), user.getPatronymic()));
        dto.setPhone(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setPosition(user.getPosition() != null ? user.getPosition().getId() : null);
        dto.setOrganization(user.getOrganization() != null ? user.getOrganization().getId() : null);
        dto.setSpeciality(user.getSpeciality() != null ? user.getSpeciality().getId() : null);

        return dto;
    }
}
