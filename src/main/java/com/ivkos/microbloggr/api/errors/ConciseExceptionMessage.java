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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

class ConciseExceptionMessage
{
    public final int status;
    public final String message;
    public final Instant timestamp = Instant.now();

    public ConciseExceptionMessage(int status, Throwable t)
    {
        this.status = status;
        this.message = t.getMessage();
    }

    public ConciseExceptionMessage(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

    public static ResponseEntity of(int code, Throwable t)
    {
        ConciseExceptionMessage msg = new ConciseExceptionMessage(code, t);

        return new ResponseEntity<>(
            msg,
            HttpStatus.valueOf(code)
        );
    }

    public static ResponseEntity of(int code, String message)
    {
        ConciseExceptionMessage msg = new ConciseExceptionMessage(code, message);

        return new ResponseEntity<>(
            msg,
            HttpStatus.valueOf(code)
        );
    }
}
