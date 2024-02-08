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

package ua.mibal.booking.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.mibal.booking.model.dto.auth.AuthResponseDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.model.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.model.dto.response.UserDto;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.NotificationSettings;
import ua.mibal.booking.model.mapper.linker.UserPhotoLinker;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(
        componentModel = SPRING,
        injectionStrategy = CONSTRUCTOR, uses = {
        UserPhotoLinker.class, PhoneMapper.class
})
public interface UserMapper {

    @Mapping(target = "password", source = "encodedPassword")
    User toEntity(RegistrationDto registrationDto, String encodedPassword);

    @Mapping(target = "token", source = "jwtToken")
    AuthResponseDto toAuthResponse(User user, String jwtToken);

    @Mapping(target = "photo", source = "user")
    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void update(@MappingTarget User user, ChangeUserDetailsDto changeUserDetailsDto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void update(@MappingTarget NotificationSettings notificationSettings,
                ChangeNotificationSettingsDto changeNotificationSettingsDto);
}
