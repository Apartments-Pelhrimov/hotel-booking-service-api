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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.booking.application.dto.response.calendar.Calendar;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.domain.HotelTurningOffTime;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class CalendarService_UnitTest {

    private CalendarService service;

    @Mock
    private ReservationSystemManager reservationSystemManager;
    @Mock
    private ApartmentInstanceService apartmentInstanceService;
    @Mock
    private ApartmentService apartmentService;
    @Mock
    private TurningOffService turningOffService;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentInstance apartmentInstance;

    private final Event apartmentInstanceEvent = spy(Event.from(
            now(), now().plusDays(1), "ApartmentInstanceEvent"
    ));
    private final Event bookingComEvent = spy(Event.from(
            now(), now().plusDays(1), "BookingComEvent"
    ));
    private final HotelTurningOffTime hotelTurningOffTime = spy(new HotelTurningOffTime(
            1L, now(), now().plusDays(2), "Christmas holidays"
    ));

    @BeforeEach
    void setup() {
        service = new CalendarService(reservationSystemManager, apartmentInstanceService, apartmentService, turningOffService);
    }

    @Test
    void getCalendarsForApartment() {
        Long apartmentId = 1L;

        when(apartmentService.getOneFetchInstances(apartmentId))
                .thenReturn(apartment);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));
        when(apartment.getApartmentInstances())
                .thenReturn(List.of(apartmentInstance));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(reservationSystemManager.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<Event> expectedEvents =
                List.of(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
        List<Calendar> expected = List.of(Calendar.of(expectedEvents));

        List<Calendar> actual = service.getCalendarsForApartment(apartmentId);

        assertEquals(expected, actual);
    }

    @Test
    void getCalendarForApartmentInstance() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(turningOffService.getForHotelForNow())
                .thenReturn(List.of(hotelTurningOffTime));

        when(apartmentInstance.getNotRejectedEventsForNow())
                .thenReturn(List.of(apartmentInstanceEvent));
        when(reservationSystemManager.getEventsFor(apartmentInstance))
                .thenReturn(List.of(bookingComEvent));

        List<Event> expectedEvents =
                List.of(apartmentInstanceEvent, hotelTurningOffTime, bookingComEvent);
        Calendar expected = Calendar.of(expectedEvents);

        Calendar actual = service.getCalendarForApartmentInstance(instanceId);

        assertEquals(expected, actual);
    }
}
