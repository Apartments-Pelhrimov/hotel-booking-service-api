/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.model.dto.response;

import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public record ApartmentCardDto(
        String name,
        Set<String> photos,
        ApartmentOptions options,
        Double rating,
        List<Bed> beds,
        BigDecimal cost
) {
}