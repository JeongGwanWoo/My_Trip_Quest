package com.mytripquest.domain.user.dto;

import com.mytripquest.domain.user.entity.User;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDto {
    private User.Role role;
}
