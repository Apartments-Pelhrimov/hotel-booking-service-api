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

package ua.mibal.booking.adapter.out.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.mibal.booking.application.port.jpa.ReservationRepository;
import ua.mibal.booking.domain.Reservation;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long>, ReservationRepository {

    @Query("""
            select r from Reservation r
                left join fetch r.apartmentInstance ai
                left join fetch ai.apartment a
                left join fetch a.photos
                left join fetch r.user u
            where u.email = ?1
            """)
    Page<Reservation> findAllByUserEmail(String email, Pageable pageable);

    @Query("""
            select r from Reservation r
                left join fetch r.rejections rr
            where r.id = ?1
            """)
    Optional<Reservation> findByIdFetchRejections(Long id);

    @Query("""
            select count(r.id) > 0
                from Reservation r
            where
                r.state != 'REJECTED'
                and r.details.to > ?1 and r.details.from < ?2
            """)
    boolean existsReservationThatIntersectsRange(LocalDateTime start, LocalDateTime end);
}
