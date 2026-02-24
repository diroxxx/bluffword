package org.project.backend_kotlin.config.customException

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

data class ApiErrorResponse(
    val message: String
)

class ApiExceptionHandler(
) {

    @ExceptionHandler(ApiCustomException::class)
    fun handle(ex: ApiCustomException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(ex.status).body(ApiErrorResponse(message = ex.message ?: "Error"))

}
