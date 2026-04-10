package cn.edu.hdu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的 API 接口响应结果封装类
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;   // 状态码
    private String msg;     // 提示信息
    private T data;         // 数据

    // 私有构造，强制使用静态方法创建对象
    private Result() {}

    // 成功响应，不带数据
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("成功");
        return r;
    }

    // 成功响应，带数据
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }

    // 失败响应
    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(message);
        return r;
    }

    // 自定义状态码和信息
    public static <T> Result<T> custom(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(message);
        return r;
    }
}