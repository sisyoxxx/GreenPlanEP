// src/main/java/cn/edu/hdu/pojo/User.java
package cn.edu.hdu.pojo;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
}