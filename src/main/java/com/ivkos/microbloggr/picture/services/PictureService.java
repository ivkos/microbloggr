/*
 * Copyright 2018 Ivaylo Stoyanov <me@ivkos.com>
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

package com.ivkos.microbloggr.picture.services;

import com.ivkos.microbloggr.picture.models.Picture;
import com.ivkos.microbloggr.support.service.CreatableEntityService;
import com.ivkos.microbloggr.support.service.DeletableEntityService;
import com.ivkos.microbloggr.support.service.ReadableEntityService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PictureService extends
    CreatableEntityService<Picture>,
    ReadableEntityService<Picture, UUID>,
    DeletableEntityService<Picture, UUID>
{
    Picture create(MultipartFile file);

    Resource readAsResource(Picture picture);

    void delete(Picture entity, boolean deleteFromStorage);

    void delete(UUID uuid, boolean deleteFromStorage);
}
