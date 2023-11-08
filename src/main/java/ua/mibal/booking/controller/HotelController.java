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

package ua.mibal.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.model.dto.HotelDto;
import ua.mibal.booking.model.search.SearchFilters;
import ua.mibal.booking.model.search.SearchOptions;
import ua.mibal.booking.model.search.SearchParams;
import ua.mibal.booking.service.HotelService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    public Page<HotelDto> getAll(SearchParams searchParams,
                                 SearchFilters searchFilters,
                                 SearchOptions searchOptions,
                                 Pageable pageable) {
        return hotelService.findAll(searchParams, searchFilters, searchOptions, pageable);
    }


}
