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

package ua.mibal.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.mibal.booking.model.entity.ApartmentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<ApartmentType, Long> {

    @Query("""
            select a from ApartmentType a
                left join fetch a.hotel
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<ApartmentType> findByIdFetchPhotosHotel(Long id);

    @Query("""
            select a from ApartmentType a
                left join fetch a.photos
            """)
    List<ApartmentType> findAllFetchPhotos();

    @Query("""
            select a from ApartmentType a
                left join fetch a.photos
            where a.id = ?1
            """)
    Optional<ApartmentType> findByIdFetchPhotos(Long id);
}