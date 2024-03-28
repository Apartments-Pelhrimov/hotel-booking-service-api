/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.application.exception;

import ua.mibal.booking.domain.Token;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public abstract class EntityNotFoundException extends NotFoundException {

    // TODO fix exceptions

    protected EntityNotFoundException(Class<?> entity, Object id) {
        super(
                "not-found-error.entity.by-id",
                entity.getSimpleName(), id
        );
    }

    protected EntityNotFoundException(String userEmail) {
        super("not-found-error.entity.user-by-email", userEmail);
    }

    protected EntityNotFoundException(Integer people) {
        super("not-found-error.entity.price-by-composite", people);
    }

    protected EntityNotFoundException(Class<Token> entity, String tokenValue) {
        super("not-found-error.entity.token-by-value", tokenValue);
    }
}
