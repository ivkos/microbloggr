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

package com.ivkos.microbloggr.post.services;

import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.models.PostLike;
import com.ivkos.microbloggr.post.models.PostLikeId;
import com.ivkos.microbloggr.support.service.CreatableEntityService;
import com.ivkos.microbloggr.support.service.DeletableEntityService;
import com.ivkos.microbloggr.support.service.ReadableEntityService;
import com.ivkos.microbloggr.user.models.User;

import java.util.List;
import java.util.Optional;

public interface PostLikeService extends
    CreatableEntityService<PostLike>,
    ReadableEntityService<PostLike, PostLikeId>,
    DeletableEntityService<PostLike, PostLikeId>
{
    Optional<PostLike> find(User liker, Post post);

    PostLike likePost(User actor, Post post);

    void unlikePost(User actor, Post post);

    boolean doesLike(User viewer, Post post);

    List<User> getLikersOfPost(Post post);

    long getLikeCountOfPost(Post post);
}
