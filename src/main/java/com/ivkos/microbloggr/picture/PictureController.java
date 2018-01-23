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

package com.ivkos.microbloggr.picture;

import com.ivkos.microbloggr.support.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pictures")
class PictureController
{
    private final PictureService pictureService;

    @Autowired
    PictureController(PictureService pictureService) {this.pictureService = pictureService;}

    @GetMapping("/")
    @Secured(Role.ADMIN)
    public List<Picture> listAllPictures()
    {
        return pictureService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getPicture(@PathVariable UUID id)
    {
        Picture picture = pictureService.findById(id);
        Resource resource = pictureService.readAsResource(picture);

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\""
            )
            .body(resource);
    }

    @PostMapping("/")
    public Picture upload(@RequestParam("file") MultipartFile file)
    {
        return pictureService.create(file);
    }
}
