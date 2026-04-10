package cn.edu.hdu.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    // 前端只传这两个，这里就写这两个
    private String username;
    private String password;
}