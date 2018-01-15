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
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.ivkos.microbloggr.post.models.PostType.TEXT;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostLikeServiceTest
{
    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private PostLikeService postLikeService;

    private User user;
    private Post post;

    @Before
    public void setUp() throws Exception
    {
        user = userService.createUser("me@ivkos.com", "password", "ivkos");
        post = postService.createPost(user, TEXT, "Hello world");
    }

    @Test
    public void thatItIsNotLiked()
    {
        assertThat(postLikeService.doesLike(user, post)).isFalse();
        assertThat(postLikeService.find(user, post).isPresent()).isFalse();
    }

    @Test
    public void thatIsLiked()
    {
        PostLike like = postLikeService.likePost(user, post);
        assertThat(like).isNotNull();

        assertThat(postLikeService.find(user, post).isPresent()).isTrue();
        assertThat(postLikeService.doesLike(user, post)).isTrue();
        assertThat(postLikeService.getLikersOfPost(post)).contains(user);
        assertThat(postLikeService.getLikeCountOfPost(post)).isEqualTo(1);
    }

    @Test
    public void thatIsUnliked()
    {
        thatIsLiked();

        postLikeService.unlikePost(user, post);

        assertThat(postLikeService.find(user, post).isPresent()).isFalse();
        assertThat(postLikeService.doesLike(user, post)).isFalse();
        assertThat(postLikeService.getLikersOfPost(post)).doesNotContain(user);
        assertThat(postLikeService.getLikeCountOfPost(post)).isEqualTo(0);
    }
}
