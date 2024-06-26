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

package ua.mibal.booking.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.application.dto.request.CreateCommentDto;
import ua.mibal.booking.application.dto.response.CommentDto;
import ua.mibal.booking.application.mapper.linker.UserPhotoLinker;
import ua.mibal.booking.domain.Comment;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(uses = UserPhotoLinker.class,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = ".", source = "comment.user")
    @Mapping(target = "userPhotoLink", source = "comment.user")
    CommentDto toDto(Comment comment);

    Comment toEntity(CreateCommentDto createCommentDto);
}
