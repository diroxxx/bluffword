package org.project.backend_kotlin.config.customException

import org.springframework.http.HttpStatus

class ApiCustomException(
    val status: HttpStatus,
    message: String
) : RuntimeException(message)