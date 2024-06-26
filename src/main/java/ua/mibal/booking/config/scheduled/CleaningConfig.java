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

package ua.mibal.booking.config.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.mibal.booking.application.TokenService;
import ua.mibal.booking.application.UserService;

import static java.util.concurrent.TimeUnit.HOURS;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Configuration
public class CleaningConfig {
    private final TokenService tokenService;
    private final UserService userService;

    @Scheduled(fixedDelay = 2, timeUnit = HOURS)
    void clearExpiredTokensAndRegistrations() {
        int tokensDeleted = tokenService.clearExpiredTokens();
        int usersDeleted = userService.clearNotEnabledWithNoTokens();
        log.info("{} Tokens deleted", tokensDeleted);
        log.info("{} Users deleted", usersDeleted);
    }
}
