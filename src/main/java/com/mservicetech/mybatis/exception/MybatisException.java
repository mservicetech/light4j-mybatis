/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mservicetech.mybatis.exception;

import com.networknt.exception.ClientException;
import com.networknt.status.Status;

/**
 * This is a checked exception used by light4j-mybatis.
 *
 * @author Gavin Chen
 */
public class MybatisException extends Exception {
    private static final long serialVersionUID = 1L;
    private static Status status = new Status();

    public MybatisException() {
        super();
    }

    public MybatisException(String message) {
        super(message);
    }

    public MybatisException(Status status) {
        this.status = status;
    }

    public MybatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisException(Throwable cause) {
        super(cause);
    }

    public static Status getStatus() {
        return status;
    }

    public static void setStatus(Status status) {
        MybatisException.status = status;
    }
}
