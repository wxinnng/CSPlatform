package com.csplatform.common.resp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author WangXing
 * @Date 2025/12/28 13:19
 * @PackageName:com.csplatform.common.resp
 * @ClassName: ResultData
 * @Version 1.0
 */

@Data
@Accessors(chain = true)
public class ResultData<T> {

    private String code;
    private String message;
    private T data;
    private long timestamp;//调用方法的时间戳

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCodeEnum.RC200.getCode());
        resultData.setMessage(ReturnCodeEnum.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(String code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMessage(message);

        return resultData;
    }
}