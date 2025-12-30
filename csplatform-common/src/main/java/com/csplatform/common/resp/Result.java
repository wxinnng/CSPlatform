package com.csplatform.common.resp;

/**
 * @Author WangXing
 * @Date 2025/12/30 16:42
 * @PackageName:com.csplatform.common.resp
 * @ClassName: Result
 * @Version 1.0
 */
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一响应结果
 */
@Data
@Accessors(chain = true)
public class Result<T> {


    private Integer code;

    private String message;

    private T data;

    private Long timestamp = System.currentTimeMillis();

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail() {
        return fail("操作失败");
    }

    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}