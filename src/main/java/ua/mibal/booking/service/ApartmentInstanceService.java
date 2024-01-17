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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentInstanceService {
    private final ApartmentInstanceRepository apartmentInstanceRepository;
    private final ApartmentRepository apartmentRepository;
    private final BookingComReservationService bookingComReservationService;
    private final ApartmentInstanceMapper apartmentInstanceMapper;
    private final ReservationRequestMapper reservationRequestMapper;

    public ApartmentInstance getFreeOne(Long apartmentId,
                                        ReservationRequestDto requestDto) {
        ReservationRequest request =
                reservationRequestMapper.toReservationRequest(requestDto, apartmentId);
        List<ApartmentInstance> free = getFree(request);
        return selectMostSuitable(free, request);
    }

    public void create(Long apartmentId,
                       CreateApartmentInstanceDto createApartmentInstanceDto) {
        validateApartmentExists(apartmentId);
        ApartmentInstance newInstance =
                apartmentInstanceMapper.toEntity(createApartmentInstanceDto);
        Apartment apartmentReference = apartmentRepository.getReferenceById(apartmentId);
        newInstance.setApartment(apartmentReference);
        apartmentInstanceRepository.save(newInstance);
    }

    public void delete(Long id) {
        validateApartmentInstanceExists(id);
        apartmentInstanceRepository.deleteById(id);
    }

    private List<ApartmentInstance> getFree(ReservationRequest reservationRequest) {
        List<ApartmentInstance> free = getFreeLocal(reservationRequest);
        filterFreeAtBookingCom(free, reservationRequest);
        return free;
    }

    private List<ApartmentInstance> getFreeLocal(ReservationRequest reservationRequest) {
        List<ApartmentInstance> freeLocal = apartmentInstanceRepository.findFreeByRequest(
                reservationRequest.apartmentId(),
                reservationRequest.from(),
                reservationRequest.to(),
                reservationRequest.people()
        );
        return new ArrayList<>(freeLocal);
    }

    private void filterFreeAtBookingCom(List<ApartmentInstance> freeLocal,
                                        ReservationRequest reservationRequest) {
        Predicate<ApartmentInstance> isNotFreeAtBookingCom =
                apartmentInstance -> !bookingComReservationService.isFreeForReservation(
                        apartmentInstance,
                        reservationRequest
                );
        freeLocal.removeIf(isNotFreeAtBookingCom);
    }

    private ApartmentInstance selectMostSuitable(List<ApartmentInstance> variants,
                                                 ReservationRequest request) {
        if (variants.isEmpty()) {
            throw new ApartmentIsNotAvialableForReservation(request);
        }
        if (variants.size() == 1) {
            return variants.get(0);
        }
        // TODO implement logic
        return variants.get(0);
    }

    private void validateApartmentExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }

    private void validateApartmentInstanceExists(Long id) {
        if (!apartmentInstanceRepository.existsById(id)) {
            throw new ApartmentInstanceNotFoundException(id);
        }
    }
}