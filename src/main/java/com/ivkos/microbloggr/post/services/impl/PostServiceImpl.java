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

package com.ivkos.microbloggr.post.services.impl;

import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.models.PostType;
import com.ivkos.microbloggr.post.repositories.PostRepository;
import com.ivkos.microbloggr.post.services.PostService;
import com.ivkos.microbloggr.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class PostServiceImpl implements PostService
{
    private final PostRepository repository;

    @Autowired
    PostServiceImpl(PostRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Post findById(UUID id)
    {
        return Optional.ofNullable(repository.findOne(id))
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    @Override
    public List<Post> getPostsByUser(User user)
    {
        return repository.findAllByAuthorOrderByCreatedAtDesc(user)
            .collect(Collectors.toList());
    }

    @Override
    public List<Post> getPostsByUserByType(User user, PostType type)
    {
        return repository.findAllByAuthorAndTypeOrderByCreatedAtDesc(user, type)
            .collect(Collectors.toList());
    }

    @Override
    public List<Post> getPostsByMultipleUsers(Collection<User> users)
    {
        return repository.findAllByAuthorInOrderByCreatedAtDesc(users)
            .collect(Collectors.toList());
    }

    @Override
    public Post createPost(User author, PostType type, String content)
    {
        Post post = new Post(author, type, content);

        return repository.save(post);
    }

    @Override
    public Post updatePost(Post post)
    {
        return repository.save(post);
    }

    @Override
    public void deletePost(Post post)
    {
        // TODO Refactor
        Post thePost = repository.findOne(post.getId());

        if (thePost == null) return;

        repository.delete(post);
    }
}
