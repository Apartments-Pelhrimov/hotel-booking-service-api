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

package ua.mibal.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.dto.request.PriceDto;
import ua.mibal.booking.application.mapper.PriceMapper;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Price;
import ua.mibal.booking.application.exception.PriceNotFoundException;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class PriceService {
    private final ApartmentService apartmentService;
    private final PriceMapper priceMapper;

    public List<PriceDto> getAllByApartment(Long apartmentId) {
        return apartmentService.getOneFetchPrices(apartmentId)
                .getPrices().stream()
                .map(priceMapper::toDto)
                .toList();
    }

    @Transactional
    public void put(Long apartmentId, PriceDto priceDto) {
        Price price = priceMapper.toEntity(priceDto);
        Apartment apartment = apartmentService.getOneFetchPrices(apartmentId);
        apartment.putPrice(price);
    }

    @Transactional
    public void delete(Long apartmentId, Integer person) {
        Apartment apartment = apartmentService.getOneFetchPrices(apartmentId);
        if (!apartment.deletePrice(person)) {
            throw new PriceNotFoundException(person);
        }
    }
}
