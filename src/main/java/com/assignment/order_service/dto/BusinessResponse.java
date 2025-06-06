package com.assignment.order_service.dto;

import com.assignment.order_service.exception.ErrorCode;
import com.assignment.order_service.exception.SuccessCode;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "공통 Response Body")
public class BusinessResponse<T> {

    @Schema(description = "응답 코드", example = "ORDER_CREATED")
    private String resultCode;

    @Schema(description = "응답 메세지", example = "주문이 성공적으로 생성되었습니다.")
    private String resultMessage;

    @Schema(description = "응답 본문 데이터")
    private T resultData;

    /**
     * 성공 response(resultData 있는 경우)
     * @param code
     * @param resultData
     * @return
     * @param <T>
     */
    public static <T> BusinessResponse<T> success(SuccessCode code, T resultData) {
        return new BusinessResponse<>(code.name(), code.getMessage(), resultData);
    }

    /**
     * 성공 response(resultData 없는 경우)
     * @param code
     * @return
     * @param <T>
     */
    public static <T> BusinessResponse<T> success(SuccessCode code) {
        return new BusinessResponse<>(code.name(), code.getMessage(),null);
    }


    /**
     * 실패 response
     * @param code
     * @return
     */
    public static BusinessResponse<Void> fail(ErrorCode code) {
        return new BusinessResponse<>(code.name(), code.getMessage(), null);
    }

}
