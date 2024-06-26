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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mibal.booking.application.dto.auth.RegistrationDto;
import ua.mibal.booking.application.dto.request.ChangeNotificationSettingsDto;
import ua.mibal.booking.application.dto.request.ChangePasswordDto;
import ua.mibal.booking.application.dto.request.ChangeUserDetailsDto;
import ua.mibal.booking.application.dto.request.DeleteMeDto;
import ua.mibal.booking.application.dto.response.UserDto;
import ua.mibal.booking.application.mapper.UserMapper;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.NotificationSettings;
import ua.mibal.booking.domain.Photo;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.application.exception.IllegalPasswordException;
import ua.mibal.booking.application.exception.UserNotFoundException;
import ua.mibal.test.annotation.UnitTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class UserService_UnitTest {

    private UserService service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user;
    @Mock
    private NotificationSettings notificationSettings;
    @Mock
    private UserDto userDto;
    @Mock
    private RegistrationDto registrationDto;
    @Mock
    private ChangePasswordDto changePasswordDto;
    @Mock
    private DeleteMeDto deleteMeDto;
    @Mock
    private ChangeUserDetailsDto changeUserDetailsDto;
    @Mock
    private ChangeNotificationSettingsDto changeNotificationSettingsDto;

    @BeforeEach
    void setup() {
        service = new UserService(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void getOneDto() {
        String email = "existing_email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        var actual = assertDoesNotThrow(
                () -> service.getOneDto(email)
        );

        assertEquals(userDto, actual);
    }

    @Test
    void getOneDto_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing_email";

        when(userRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.getOneDto(notExistingEmail)
        );
    }

    @Test
    void getOne() {
        String email = "existing_email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User actual = assertDoesNotThrow(
                () -> service.getOne(email)
        );

        assertEquals(user, actual);
    }

    @Test
    void getOne_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing_email";

        when(userRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.getOne(notExistingEmail)
        );
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void isExistsByEmail(boolean value) {
        String email = "email";

        when(userRepository.existsByEmail(email))
                .thenReturn(value);

        boolean actual = service.isExistsByEmail(email);

        assertEquals(value, actual);
    }

    @Test
    void delete() {
        String pass = "password";
        String email = "email";

        when(deleteMeDto.password()).thenReturn(pass);

        when(userRepository.findPasswordByEmail(email))
                .thenReturn(Optional.of(pass));
        when(passwordEncoder.matches(pass, pass))
                .thenReturn(true);

        service.delete(deleteMeDto, email);

        verify(userRepository, times(1))
                .deleteByEmail(email);
    }

    @Test
    void delete_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing_email";

        when(userRepository.findPasswordByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        verifyNoMoreInteractions(userRepository, passwordEncoder);

        assertThrows(
                UserNotFoundException.class,
                () -> service.delete(deleteMeDto, notExistingEmail)
        );
    }

    @Test
    void delete_should_throw_IllegalPasswordException() {
        String pass = "password";
        String email = "email";

        when(deleteMeDto.password()).thenReturn(pass);

        when(userRepository.findPasswordByEmail(email))
                .thenReturn(Optional.of(pass));
        when(passwordEncoder.matches(pass, pass))
                .thenReturn(false);

        verifyNoMoreInteractions(userRepository);

        assertThrows(
                IllegalPasswordException.class,
                () -> service.delete(deleteMeDto, email)
        );
    }

    @Test
    void save() {
        String pass = "password";
        String encodedPass = "encoded_password";

        when(registrationDto.password()).thenReturn(pass);

        when(passwordEncoder.encode(pass))
                .thenReturn(encodedPass);
        when(userMapper.toEntity(registrationDto, encodedPass))
                .thenReturn(user);

        service.save(registrationDto);

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void changePassword() {
        String email = "email";
        String oldOriginalPass = "password";
        String oldPass = "password";
        String newPass = "new_pass";
        String encodedNewPass = "encoded_new_pass";

        when(changePasswordDto.oldPassword()).thenReturn(oldPass);
        when(changePasswordDto.newPassword()).thenReturn(newPass);

        when(userRepository.findPasswordByEmail(email))
                .thenReturn(Optional.of(oldOriginalPass));
        when(passwordEncoder.matches(oldOriginalPass, oldPass))
                .thenReturn(true);
        when(passwordEncoder.encode(newPass))
                .thenReturn(encodedNewPass);

        assertDoesNotThrow(
                () -> service.changePassword(changePasswordDto, email)
        );

        verify(userRepository, times(1))
                .updateUserPasswordByEmail(encodedNewPass, email);
    }

    @Test
    void changePassword_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing";

        when(userRepository.findPasswordByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        verifyNoMoreInteractions(userRepository);

        assertThrows(
                UserNotFoundException.class,
                () -> service.changePassword(changePasswordDto, notExistingEmail)
        );
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePassword_should_throw_IllegalPasswordException() {
        String email = "email";
        String oldOriginalPass = "oldOriginalPassword";
        String oldPass = "oldPassword";

        when(changePasswordDto.oldPassword()).thenReturn(oldPass);

        when(userRepository.findPasswordByEmail(email))
                .thenReturn(Optional.of(oldOriginalPass));
        when(passwordEncoder.matches(oldPass, oldOriginalPass))
                .thenReturn(false);

        verifyNoMoreInteractions(userRepository, passwordEncoder);

        assertThrows(
                IllegalPasswordException.class,
                () -> service.changePassword(changePasswordDto, email)
        );
    }

    @Test
    void changeDetails_should_update_dynamic() {
        String email = "email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        service.changeDetails(changeUserDetailsDto, email);

        verify(userMapper, times(1))
                .update(user, changeUserDetailsDto);
    }

    @Test
    void changeDetails_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing_email";

        when(userRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.changeDetails(changeUserDetailsDto, notExistingEmail)
        );
    }

    @Test
    void changeNotificationSettings_should_throw_UserNotFoundException() {
        String notExistingEmail = "not_existing_email";

        when(userRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.changeNotificationSettings(changeNotificationSettingsDto, notExistingEmail)
        );
    }

    @Test
    void changeNotificationSettings_should_update_dynamic() {
        String email = "email";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getNotificationSettings())
                .thenReturn(notificationSettings);

        service.changeNotificationSettings(changeNotificationSettingsDto, email);

        verify(userMapper, only())
                .update(notificationSettings, changeNotificationSettingsDto);
    }


    @Test
    void setNewPasswordForUser() {
        Long userId = 1L;
        String pass = "pass";
        String encodedPass = "encoded_pass";

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(pass))
                .thenReturn(encodedPass);

        service.setNewPasswordForUser(userId, pass);

        verify(user, times(1))
                .setPassword(encodedPass);
    }

    @Test
    void setNewPasswordForUser_should_throw_UserNotFoundException() {
        Long userId = 1L;
        String pass = "pass";

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.setNewPasswordForUser(userId, pass));

        verify(user, never()).setPassword(any());
    }

    @Test
    void activateUserById() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        service.activateUserById(userId);

        verify(user, times(1)).enable();
    }

    @Test
    void activateUserById_should_throw_UserNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.activateUserById(userId));

        verify(user, never()).enable();
    }

    @Test
    void changeUserPhoto() {
        String email = "emaiil";
        String link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        service.changeUserPhoto(email, link);

        verify(userRepository, times(1))
                .updateUserPhotoByEmail(new Photo(link), email);
    }

    @Test
    void clearNotEnabledWithNoTokens() {
        int count = 100500;

        when(userRepository.deleteNotEnabledWithNoTokens())
                .thenReturn(count);

        int actual = service.clearNotEnabledWithNoTokens();

        assertEquals(count, actual);
    }
}
