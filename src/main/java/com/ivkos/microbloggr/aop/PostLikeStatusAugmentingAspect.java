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

package com.ivkos.microbloggr.aop;

import com.ivkos.microbloggr.post.models.Post;
import com.ivkos.microbloggr.post.services.PostLikeService;
import com.ivkos.microbloggr.user.models.User;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
class PostLikeStatusAugmentingAspect extends AbstractAuthenticationPrincipalAwareEntityAugmentingAspect<Post>
{
    private final PostLikeService postLikeService;

    @Autowired
    PostLikeStatusAugmentingAspect(PostLikeService postLikeService)
    {
        this.postLikeService = postLikeService;
    }

    @Override
    protected void augment(Post post, Object authenticationPrincipal)
    {
        if (authenticationPrincipal == null) return;

        if (post.getLikes() == null) {
            post.setLikes(postLikeService.getLikeCountOfPost(post));
        }

        if (post.getLiked() == null) {
            post.setLiked(postLikeService.doesLike((User) authenticationPrincipal, post));
        }
    }

    @AfterReturning(
        value = "execution(com.ivkos.microbloggr.post.models.Post *(..))",
        returning = "post"
    )
    void singlePostAdvice(Post post)
    {
        augment(post, getAuthenticationPrincipal());
    }

    @AfterReturning(
        value = "execution(java.util.List<com.ivkos.microbloggr.post.models.Post> *(..))",
        returning = "postList"
    )
    void postListAdvice(List<Post> postList)
    {
        Object authenticationPrincipal = getAuthenticationPrincipal();
        postList.forEach(post -> augment(post, authenticationPrincipal));
    }
}
