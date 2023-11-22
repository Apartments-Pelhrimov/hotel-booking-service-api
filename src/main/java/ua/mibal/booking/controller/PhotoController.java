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

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.service.photo.PhotoStorageService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhotoController {
    private final PhotoStorageService photoStorageService;

    @RolesAllowed("USER")
    @PutMapping("/users/me/photo")
    public String changeUserPhoto(@RequestParam("file") MultipartFile file,
                                  Authentication authentication) {
        return photoStorageService.saveUserPhoto(authentication.getName(), file);
    }

    @RolesAllowed("MANAGER")
    @PostMapping("/hotels/{id}/photos")
    public String addHotelPhoto(@PathVariable Long id,
                                @RequestParam("file") MultipartFile file) {
        return photoStorageService.saveHotelPhoto(id, file);
    }

    @RolesAllowed("MANAGER")
    @PostMapping("/hotels/apartments/{id}/photos")
    public String addApartmentPhoto(@PathVariable Long id,
                                    @RequestParam("file") MultipartFile file) {
        return photoStorageService.saveApartmentPhoto(id, file);
    }

    @RolesAllowed("MANAGER")
    @DeleteMapping("/hotels/{id}/photos")
    public String deleteHotelPhoto(@PathVariable Long id,
                                   @RequestParam("link") String link) {
        return photoStorageService.deleteHotelPhoto(id, link);
    }

    @RolesAllowed("MANAGER")
    @DeleteMapping("/hotels/apartments/{id}/photos")
    public String deleteApartmentPhoto(@PathVariable Long id,
                                       @RequestParam("link") String link) {
        return photoStorageService.deleteApartmentPhoto(id, link);
    }
}