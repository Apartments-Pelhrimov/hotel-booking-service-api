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

package ua.mibal.booking.service.photo.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.service.ApartmentService;
import ua.mibal.booking.service.UserService;
import ua.mibal.booking.service.photo.PhotoService;
import ua.mibal.booking.service.photo.aws.components.AwsStorage;
import ua.mibal.booking.service.photo.aws.model.ApartmentAwsPhoto;
import ua.mibal.booking.service.photo.aws.model.AwsPhoto;
import ua.mibal.booking.service.photo.aws.model.UserAwsPhoto;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class AwsPhotoService implements PhotoService {
    private final AwsStorage storage;
    private final UserService userService;
    private final ApartmentService apartmentService;

    @Transactional
    @Override
    public String changeUserPhoto(String email, MultipartFile photoFile) {
        AwsPhoto photo = UserAwsPhoto.getInstanceToUpload(email, photoFile);
        String link = storage.uploadPhoto(photo);
        userService.changeUserPhoto(email, link);
        return link;
    }

    @Transactional
    @Override
    public void deleteUserPhoto(String email) {
        AwsPhoto photo = UserAwsPhoto.getInstanceToDelete(email);
        storage.deletePhoto(photo);
        userService.deleteUserPhotoByEmail(email);
    }

    @Transactional
    @Override
    public String createApartmentPhoto(Long id, MultipartFile photoFile) {
        Apartment apartment = apartmentService.getOne(id);
        AwsPhoto photo = ApartmentAwsPhoto.getInstanceToUpload(id, photoFile);
        String link = storage.uploadPhoto(photo);
        apartment.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteApartmentPhoto(Long id, String link) {
        apartmentService.validateApartmentHasPhoto(id, link);
        AwsPhoto photo = ApartmentAwsPhoto.getInstanceToDelete(link);
        storage.deletePhoto(photo);
        apartmentService.deleteApartmentPhotoByLink(id, link);
    }
}