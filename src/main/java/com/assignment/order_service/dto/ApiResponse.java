package com.assignment.order_service.dto;

import com.assignment.order_service.exception.ErrorCode;
import com.assignment.order_service.exception.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 Response Body
 * @param <T>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String resultCode;
    private String resultMessage;
    private T resultData;

    /**
     * 성공 response(resultData 있는 경우)
     * @param code
     * @param resultData
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<T> success(SuccessCode code, T resultData) {
        return new ApiResponse<>(code.name(), code.getMessage(), resultData);
    }

    /**
     * 성공 response(resultData 없는 경우)
     * @param code
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<T> success(SuccessCode code) {
        return new ApiResponse<>(code.name(), code.getMessage(),null);
    }


    /**
     * 실패 response
     * @param code
     * @return
     */
    public static ApiResponse<Void> fail(ErrorCode code) {
        return new ApiResponse<>(code.name(), code.getMessage(), null);
    }

}
