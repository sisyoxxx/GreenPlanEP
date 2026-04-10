package cn.edu.hdu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserRole {
    ADMIN("admin"),
    BUYER("buyer"),
    STOCK_ADMIN("stock_admin");

    @EnumValue
    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
