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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
class LocalFileSystemPictureService implements PictureService
{
    private static final Path STORE_PATH = Paths.get("pictures");
    private final PictureRepository repo;

    @Autowired
    LocalFileSystemPictureService(PictureRepository repo)
    {
        this.repo = repo;
    }

    @Override
    public Picture create(Picture entity)
    {
        return repo.save(entity);
    }

    @Override
    public Picture create(MultipartFile file)
    {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Picture picture = create(new Picture());

        try {
            Path picturePath = getPathByUuid(picture.getId());
            Files.createDirectories(STORE_PATH);
            Files.copy(file.getInputStream(), picturePath);
            return picture;
        } catch (IOException e) {
            actuallyDeleteEntity(picture);
            throw new RuntimeException("Something went wrong while storing this file", e);
        }
    }

    @Override
    public Resource readAsResource(Picture picture)
    {
        Path path = getPathByUuid(picture.getId());

        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Cannot read this file");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot read this file", e);
        }
    }

    @Override
    public void delete(Picture entity, boolean deleteFromStorage)
    {
        entity.setDeleted(true);
        repo.save(entity);

        if (deleteFromStorage) {
            deleteFromFileSystem(entity.getId());
            actuallyDeleteEntity(entity);
        }
    }

    @Override
    public void delete(UUID uuid, boolean deleteFromStorage)
    {
        Picture picture = findById(uuid);
        picture.setDeleted(true);
        repo.save(picture);

        if (deleteFromStorage) {
            deleteFromFileSystem(uuid);
            actuallyDeleteEntity(picture);
        }
    }

    @Override
    public void delete(Picture entity)
    {
        delete(entity, false);
    }

    @Override
    public void delete(UUID uuid) throws EntityNotFoundException
    {
        delete(uuid, false);
    }

    @Override
    public Picture findById(UUID uuid) throws EntityNotFoundException
    {
        return Optional.ofNullable(repo.findOne(uuid))
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new EntityNotFoundException("Picture not found"));
    }

    @Override
    public List<Picture> findAll()
    {
        return StreamSupport
            .stream(repo.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    private void deleteFromFileSystem(UUID uuid)
    {
        Path path = getPathByUuid(uuid);

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    private void actuallyDeleteEntity(Picture entity)
    {
        repo.delete(entity);
    }

    private Path getPathByUuid(UUID uuid)
    {
        return STORE_PATH.resolve(Paths.get(uuid.toString()));
    }
}
