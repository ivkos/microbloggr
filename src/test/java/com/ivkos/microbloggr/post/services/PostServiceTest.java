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
import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.models.PostType;
import com.ivkos.microbloggr.user.models.User;
import com.ivkos.microbloggr.user.services.UserService;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ivkos.microbloggr.post.models.PostType.PICTURE;
import static com.ivkos.microbloggr.post.models.PostType.TEXT;
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

    private User user;
    private Post textPost;
    private Post imagePost;

    @Before
    public void setUp() throws Exception
    {
        user = userService.create("me@ivkos.com", "password", "ivkos");
        textPost = postService.createPost(user, TEXT, "Hello world");
        imagePost = postService.createPost(user, PICTURE, "https://static.ivkos.com/hi.jpg");
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
    public void thatItWillFindPostsByUserByType()
    {
        List<Post> textPosts = postService.getPostsByUserByType(user, TEXT);
        assertThat(textPosts).isNotEmpty();
        assertThat(textPosts).extracting(Post::getType).are(ofType(TEXT));

        List<Post> imagePosts = postService.getPostsByUserByType(user, PICTURE);
        assertThat(imagePosts).isNotEmpty();
        assertThat(imagePosts).extracting(Post::getType).are(ofType(PICTURE));
    }

    @Test
    public void thatPostIsDeleted()
    {
        UUID postId = imagePost.getId();
        postService.deletePost(imagePost);

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
    public void thatFeedIsEmptyWhenNoFollowees()
    {
        List<User> followees = followService.getFolloweesOfUser(user);
        assertThat(followees).isEmpty();

        List<Post> feed = postService.getFeedForUser(user);
        assertThat(feed).isEmpty();
    }

    @Test
    public void thatFeedIsNotEmptyWhenTheyHaveFollowees()
    {
        User user2 = userService.create("user2@example.com", "password", "user2");
        followService.create(user, user2);
        postService.createPost(user2, TEXT, "abracadabra");

        List<Post> feed = postService.getFeedForUser(user);
        assertThat(feed).isNotEmpty();
    }

    @Test
    public void thatPostIsUpdated()
    {
        String newContent = "POST IS UPDATED";
        Post updatedPost = postService.updatePost(textPost.setContent(newContent));

        assertThat(updatedPost.getContent()).isEqualTo(newContent);
    }

    private static Condition<PostType> ofType(PostType type)
    {
        return new Condition<PostType>()
        {
            @Override
            public boolean matches(PostType value)
            {
                return type.equals(value);
            }
        };
    }
}
