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

import net.fortuna.ical4j.model.property.ProdId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.application.exception.ICalServiceException;
import ua.mibal.booking.application.mapper.CalendarFormatMapper;
import ua.mibal.booking.config.properties.CalendarProps;
import ua.mibal.booking.domain.Event;
import ua.mibal.booking.testUtils.DataGenerator;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ua.mibal.booking.testUtils.ICalTestUtils.mustContainEvents;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ICalService.class, CalendarFormatMapper.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ICalService_UnitTest {
    private final static ZoneId zoneId = ZoneId.of("Europe/Kyiv");

    @Autowired
    private ICalService service;
    @Autowired
    private CalendarFormatMapper calendarFormatMapper;

    @MockBean
    private CalendarProps calendarProps;
    @MockBean
    private CalendarProps.ICalProps iCalProps;

    // FIXME divide into CalendarFormatMapper test and ICalService test

    @BeforeEach
    void setup() {
        when(calendarProps.zoneId()).thenReturn(zoneId);
        when(calendarProps.iCal()).thenReturn(iCalProps);
        when(iCalProps.prodId()).thenReturn(new ProdId("TEST"));
    }

    @Test
    void getCalendarFromEvents() {
        List<Event> events = DataGenerator.randomEvents();

        String calendar = service.getCalendarFromEvents(events);

        mustContainEvents(calendar, events, zoneId);
    }

    @Test
    void getEventsFromCalendarFile() {
        InputStream iCalFile = getClass().getResourceAsStream("/test.ics");
        List<Event> expected = DataGenerator.testEventsFromTestFile(zoneId);

        List<Event> actual = service.getEventsFromCalendarFile(iCalFile);

        assertEquals(expected, actual);
    }

    @Test
    void getEventsFromCalendarFile_should_throw_ICalServiceException() {
        assertThrows(ICalServiceException.class,
                () -> service.getEventsFromCalendarFile(null));
    }
}
