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

package ua.mibal.booking.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.mibal.booking.adapter.in.web.security.annotation.ManagerAllowed;
import ua.mibal.booking.application.ApartmentService;
import ua.mibal.booking.application.dto.request.CreateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentDto;
import ua.mibal.booking.application.dto.response.ApartmentCardDto;
import ua.mibal.booking.application.dto.response.ApartmentDto;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping("/{id}")
    public ApartmentDto getOne(@PathVariable Long id) {
        return apartmentService.getOneFetchPhotosBeds(id);
    }

    @GetMapping
    public List<ApartmentCardDto> getAll() {
        return apartmentService.getAllFetchPhotosBeds();
    }

    @ManagerAllowed
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid CreateApartmentDto createApartmentDto) {
        apartmentService.create(createApartmentDto);
    }

    @ManagerAllowed
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id,
                       @RequestBody @Valid UpdateApartmentDto updateApartmentDto) {
        apartmentService.update(updateApartmentDto, id);
    }

    // TODO update apartment options

    @ManagerAllowed
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        apartmentService.delete(id);
    }
}
