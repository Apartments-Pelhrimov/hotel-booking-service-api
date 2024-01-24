/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.service.photo.PhotoService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhotoController {
    private final PhotoService photoService;

    @RolesAllowed("USER")
    @PutMapping("/users/me/photo")
    @ResponseStatus(HttpStatus.CREATED)
    public String changeUserPhoto(@RequestParam("file") MultipartFile file,
                                  Authentication authentication) {
        return photoService.changeUserPhoto(authentication.getName(), file);
    }

    @RolesAllowed("USER")
    @DeleteMapping("/users/me/photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserPhoto(Authentication authentication) {
        photoService.deleteUserPhoto(authentication.getName());
    }

    @RolesAllowed("MANAGER")
    @PostMapping("/apartments/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public String createApartmentPhoto(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file) {
        return photoService.createApartmentPhoto(id, file);
    }

    @RolesAllowed("MANAGER")
    @DeleteMapping("/apartments/{id}/photos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApartmentPhoto(@PathVariable Long id,
                                     @RequestParam("link") String link) {
        photoService.deleteApartmentPhoto(id, link);
    }
}
