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

package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.exception.ApartmentIsNotAvialableForReservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentInstanceMapper;
import ua.mibal.booking.model.mapper.ReservationRequestMapper;
import ua.mibal.booking.model.request.ReservationRequest;
import ua.mibal.booking.model.request.ReservationRequestDto;
import ua.mibal.booking.repository.ApartmentInstanceRepository;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApartmentInstanceService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentInstanceService_UnitTest {

    @Autowired
    private ApartmentInstanceService service;

    @MockBean
    private ApartmentInstanceRepository apartmentInstanceRepository;
    @MockBean
    private ReservationRequestMapper reservationRequestMapper;
    @MockBean
    private ApartmentRepository apartmentRepository;
    @MockBean
    private DateTimeUtils dateTimeUtils;
    @MockBean
    private BookingComReservationService bookingComReservationService;
    @MockBean
    private ApartmentInstanceMapper apartmentInstanceMapper;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private ApartmentInstance apartmentInstance2;
    @Mock
    private Apartment apartment;
    @Mock
    private CreateApartmentInstanceDto createApartmentInstanceDto;
    @Mock
    private LocalDate dateFrom;
    @Mock
    private LocalDate dateTo;

    @Test
    public void getFreeOne() {
        Long id = 1L;
        int people = 1;
        ReservationRequest reservationRequest = new ReservationRequest(MIN, MAX, people, id);
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto(dateFrom, dateTo, people);

        when(dateTimeUtils.reserveFrom(dateFrom)).thenReturn(MIN);
        when(dateTimeUtils.reserveTo(dateTo)).thenReturn(MAX);
        when(reservationRequestMapper.toReservationRequest(reservationRequestDto, id))
                .thenReturn(reservationRequest);
        when(apartmentInstanceRepository.findFreeByRequest(id, MIN, MAX, people))
                .thenReturn(List.of(apartmentInstance2, apartmentInstance));
        when(bookingComReservationService.isFreeForReservation(apartmentInstance2, reservationRequest))
                .thenReturn(false);
        when(bookingComReservationService.isFreeForReservation(apartmentInstance, reservationRequest))
                .thenReturn(true);

        ApartmentInstance actual = service
                .getFreeOne(id, reservationRequestDto);

        assertEquals(apartmentInstance, actual);
    }

    @Test
    public void getFreeOne_should_throw_FreeApartmentsForDateNotFoundException() {
        Long id = 1L;
        int people = 1;
        ReservationRequest reservationRequest = new ReservationRequest(MIN, MAX, people, id);
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto(dateFrom, dateTo, people);

        when(dateTimeUtils.reserveFrom(dateFrom)).thenReturn(MIN);
        when(dateTimeUtils.reserveTo(dateTo)).thenReturn(MAX);
        when(reservationRequestMapper.toReservationRequest(reservationRequestDto, id))
                .thenReturn(reservationRequest);
        when(apartmentInstanceRepository.findFreeByRequest(id, MIN, MAX, people))
                .thenReturn(List.of());

        ApartmentIsNotAvialableForReservation e = assertThrows(
                ApartmentIsNotAvialableForReservation.class,
                () -> service.getFreeOne(id, reservationRequestDto)
        );

        assertEquals(
                new ApartmentIsNotAvialableForReservation(new ReservationRequest(MIN, MAX, people, id)).getMessage(),
                e.getMessage()
        );
    }


    @Test
    public void create() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(true);
        when(apartmentInstanceMapper.toEntity(createApartmentInstanceDto)).thenReturn(apartmentInstance);
        when(apartmentRepository.getReferenceById(id)).thenReturn(apartment);

        service.create(id, createApartmentInstanceDto);

        verify(apartmentInstance, times(1)).setApartment(apartment);
        verify(apartmentInstanceRepository, times(1)).save(apartmentInstance);
    }

    @Test
    public void create_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.create(id, createApartmentInstanceDto)
        );

        verifyNoInteractions(apartmentInstanceMapper, apartmentInstance, apartmentInstanceRepository);
    }

    @Test
    public void delete() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(apartmentInstanceRepository, times(1)).deleteById(id);
    }

    @Test
    public void delete_should_throw_ApartmentInstanceNotFoundException() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentInstanceNotFoundException.class,
                () -> service.delete(id)
        );

        verify(apartmentInstanceRepository, never()).deleteById(id);
    }
}