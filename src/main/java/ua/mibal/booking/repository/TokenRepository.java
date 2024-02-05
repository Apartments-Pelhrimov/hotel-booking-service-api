/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.entity.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
            select t from Token t
            where t.value = ?1
            """)
    Optional<Token> findByValue(String tokenValue);

    @Transactional
    @Modifying
    @Query("""
            delete Token t
            where t.expiresAt < now()
            """)
    int deleteExpired();
}
