/*
 * Copyright 2017 Ivaylo Stoyanov <me@ivkos.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivkos.microbloggr.api.errors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Component
@ControllerAdvice
class ApiExceptionHandler
{
    private final Logger logger;

    @Autowired
    ApiExceptionHandler(Logger logger)
    {
        this.logger = logger;
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity exception(Exception e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(500, "Something went wrong");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity illegalArgument(IllegalArgumentException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(400, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity notReadable(HttpMessageNotReadableException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(400, "The request body is unreadable");
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity authenticationException(AuthenticationException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(401, e);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity badCredentials(BadCredentialsException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(401, e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity accessDenied(AccessDeniedException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(403, e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity notFound(EntityNotFoundException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(404, e);
    }

    @ExceptionHandler(EntityExistsException.class)
    ResponseEntity entityExists(EntityExistsException e)
    {
        logThrowable(e);
        return ConciseExceptionMessage.of(409, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity validationException(MethodArgumentNotValidException e)
    {
        logThrowable(e);

        String result = e.getBindingResult().getAllErrors().stream()
            .filter(x -> x instanceof FieldError)
            .map(fe -> ((FieldError) fe).getField() + " " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));

        return ConciseExceptionMessage.of(400, result);
    }

    private void logThrowable(Throwable t)
    {
        logger.error("Something happened", t);
    }
}
