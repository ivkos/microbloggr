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

import com.ivkos.microbloggr.follow.services.FollowService;
import com.ivkos.microbloggr.picture.models.Picture;
import com.ivkos.microbloggr.picture.services.PictureService;
import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostServiceTest
{
    @Autowired private FollowService followService;
    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private PictureService pictureService;

    private User user;
    private Post textPost;
    private Post imagePost;
    private Picture picture;

    @Before
    public void setUp() throws Exception
    {
        user = userService.create("me@ivkos.com", "password", "ivkos");
        textPost = postService.create(user, "Hello world", null);

        picture = pictureService.create(new MockMultipartFile("pic.jpg", new byte[] { 0 }));
        imagePost = postService.create(user, "Some text", picture.getId());
    }

    @Test
    public void thatFindByIdWillFindExistingPost()
    {
        assertThat(postService.findById(textPost.getId())).isNotNull();
    }

    @Test(expected = EntityNotFoundException.class)
    public void thatFindByIdWontFindNonExistentPost()
    {
        UUID fakeId = UUID.randomUUID();
        postService.findById(fakeId);
    }

    @Test
    public void thatItWillFindPostsByUser()
    {
        List<Post> posts = postService.getPostsByUser(user);
        assertThat(posts).isNotEmpty();
    }

    @Test
    public void thatPostIsDeleted()
    {
        UUID postId = imagePost.getId();
        postService.delete(imagePost);

        assertThatThrownBy(() -> postService.findById(postId))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void thatPostsOfMultipleUsersIsEmptyWhenPassedAnEmptyCollection()
    {
        List<Post> posts = postService.getPostsByMultipleUsers(Collections.emptySet());
        assertThat(posts).isEmpty();
    }

    @Test
    public void thatPostsOfMultipleUsersIsNotEmpty()
    {
        List<Post> posts = postService.getPostsByMultipleUsers(Collections.singleton(user));
        assertThat(posts).isNotEmpty();
    }

    @Test
    public void thatFeedIsNotEmptyWhenTheyHaveFollowees()
    {
        User user2 = userService.create("user2@example.com", "password", "user2");
        followService.create(user, user2);
        postService.create(user2, "abracadabra", null);

        List<Post> feed = postService.getFeedForUser(user);
        assertThat(feed).isNotEmpty();
    }

    @Test
    public void thatPostIsUpdated()
    {
        String newContent = "POST IS UPDATED";
        Post updatedPost = postService.create(textPost.setContent(newContent));

        assertThat(updatedPost.getContent()).isEqualTo(newContent);
    }
}
