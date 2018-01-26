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
import com.ivkos.microbloggr.support.service.CrudEntityService;
import com.ivkos.microbloggr.user.models.User;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PostService extends CrudEntityService<Post, UUID>
{
    Post create(User author, String content, UUID pictureId);

    List<Post> getPostsByUser(User user);

    List<Post> getPostsByMultipleUsers(Collection<User> users);

    List<Post> getFeedForUser(User user);
}
