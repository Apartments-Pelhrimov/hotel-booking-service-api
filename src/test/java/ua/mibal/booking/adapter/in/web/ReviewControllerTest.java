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

package ua.mibal.booking.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.mibal.booking.application.ReviewService;
import ua.mibal.booking.application.model.CreateReviewForm;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Review;
import ua.mibal.booking.domain.User;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.mibal.booking.adapter.in.web.security.TestSecurityJwtUtils.jwt;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@WebMvcTest(ReviewController.class)
class ReviewControllerTest extends ControllerTest {
    private static final int PAGE_SIZE = 20;

    @MockBean
    private ReviewService reviewService;

    private Apartment apartment;
    private User user;

    @Test
    void getAllByApartment() throws Exception {
        givenApartment(1L);
        givenUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123");
        givenServiceWithReview(2L, "Great apartment!", 4.9);

        mvc.perform(get("/api/apartments/{apartmentId}/reviews", 1L))

                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "user": {
                              "firstName": "Mykhailo",
                              "lastName": "Balakhon",
                              "photo": "http://localhost/api/photos/photo_key_123"
                            },
                            "body": "Great apartment!",
                            "rate": 4.9
                          }
                        ]
                        """));
    }

    @Test
    void getAllLatest() throws Exception {
        givenApartment(1L);
        givenUser("Mykhailo", "Balakhon", "user@email.com", "photo_key_123");
        givenServiceWithReview(2L, "Great apartment!", 4.9);

        mvc.perform(get("/api/reviews/latest"))

                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "user": {
                              "firstName": "Mykhailo",
                              "lastName": "Balakhon",
                              "photo": "http://localhost/api/photos/photo_key_123"
                            },
                            "body": "Great apartment!",
                            "rate": 4.9
                          }
                        ]
                        """));
    }

    @Test
    void create() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", 1L)
                        .with(jwt("user@email.com", "USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isCreated());

        verify(reviewService).create(new CreateReviewForm(
                "Great apartment!", 4.9, 1L, "user@email.com"
        ));
    }

    @Test
    void createWithoutNeededAuthorities() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", 1L)
                        .with(jwt("user@email.com", "NOT_USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isForbidden());
    }

    @Test
    void createWithoutAuthorization() throws Exception {
        mvc.perform(post("/api/apartments/{apartmentId}/reviews", 1L)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "body": "Great apartment!",
                                  "rate": 4.9
                                }
                                """))

                .andExpect(status().isForbidden());
    }

    @Test
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L)
                        .with(jwt("user@email.com", "USER")))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(2L, "user@email.com");
    }

    @Test
    void deleteWithoutNeededAuthorities() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L)
                        .with(jwt("user@email.com", "NOT_USER")))
                .andExpect(status().isForbidden());

        verify(reviewService, never()).delete(2L, "user@email.com");
    }

    @Test
    void deleteWithoutAuthorization() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/reviews/{id}", 2L))
                .andExpect(status().isForbidden());

        verify(reviewService, never()).delete(2L, "user@email.com");
    }

    private void givenApartment(Long id) {
        apartment = new Apartment();
        apartment.setId(id);
    }

    private void givenUser(String firstName, String lastName, String username, String photoKey) {
        user = new User();
        user.setEmail(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoto(photoKey);
    }

    private void givenServiceWithReview(Long id, String body, double rate) {
        Review review = new Review();
        review.setId(id);
        review.setApartment(apartment);
        review.setUser(user);
        review.setBody(body);
        review.setRate(rate);

        when(reviewService.getAllByApartment(apartment.getId(), of(0, PAGE_SIZE)))
                .thenReturn(List.of(review));
        when(reviewService.getAllLatest(of(0, PAGE_SIZE)))
                .thenReturn(List.of(review));
    }
}