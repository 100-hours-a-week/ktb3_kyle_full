package com.kyle.week4.unit;

import com.kyle.week4.cache.CountCache;
import com.kyle.week4.controller.request.PostCreateRequest;
import com.kyle.week4.controller.response.PostDetailResponse;
import com.kyle.week4.entity.CommentCount;
import com.kyle.week4.entity.Post;
import com.kyle.week4.entity.User;
import com.kyle.week4.exception.CustomException;
import com.kyle.week4.fixture.post.PostRequestFixture;
import com.kyle.week4.fixture.user.UserFixture;
import com.kyle.week4.repository.comment.CommentRepository;
import com.kyle.week4.repository.post.CommentCountRepository;
import com.kyle.week4.repository.post.PostImageRepository;
import com.kyle.week4.repository.post.PostRepository;
import com.kyle.week4.repository.user.UserRepository;
import com.kyle.week4.service.PostService;
import com.kyle.week4.utils.ImageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.kyle.week4.exception.ErrorCode.GCS_IMAGE_UPLOAD_ERROR;
import static com.kyle.week4.exception.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentCountRepository commentCountRepository;
    @Mock
    private PostImageRepository postImageRepository;
    @Mock
    private CountCache postViewCountCache;
    @Mock
    private CountCache postLikeCountCache;
    @Mock
    private ImageUploader imageUploader;
    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글을 작성한다. 업로드할 이미지가 없으면 PostImage는 생성되지 않는다.")
    void createPost() {
        // given
        PostCreateRequest request = PostRequestFixture.create();
        User user = UserFixture.savedUser(1L);
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post post = invocation.getArgument(0, Post.class);
            post.assignId(1L);
            return post;
        });
        given(postImageRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));
        given(commentCountRepository.save(any(CommentCount.class))).willAnswer(inv -> inv.getArgument(0));


        // when
        PostDetailResponse response = postService.createPostAndImage(1L, request, null);

        // then
        then(imageUploader).should(never()).uploadImages(anyList());
        then(commentCountRepository).should().save(any());
        then(postRepository).should().save(captor.capture());

        Post savedPost = captor.getValue();
        assertThat(savedPost.getId()).isEqualTo(1L);
        assertThat(savedPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(request.getContent());
        assertThat(savedPost.getPostImages()).isEmpty();
    }

    @Test
    @DisplayName("게시글을 작성한다. 업로드할 이미지 수만큼 PostImage가 생성된다.")
    void createPost_when() {
        // given
        PostCreateRequest request = PostRequestFixture.create();
        User user = UserFixture.savedUser(1L);
        List<MultipartFile> images = List.of(mock(MultipartFile.class), mock(MultipartFile.class));
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post post = invocation.getArgument(0, Post.class);
            post.assignId(1L);
            return post;
        });
        given(imageUploader.uploadImages(images)).willReturn(List.of("image1", "image2"));
        given(postImageRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));
        given(commentCountRepository.save(any(CommentCount.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        PostDetailResponse response = postService.createPostAndImage(1L, request, images);

        // then
        then(imageUploader).should().uploadImages(images);
        then(postImageRepository).should().saveAll(anyList());
        then(commentCountRepository).should().save(any());
        then(postRepository).should().save(captor.capture());

        Post savedPost = captor.getValue();
        assertThat(savedPost.getId()).isEqualTo(1L);
        assertThat(savedPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(request.getContent());
        assertThat(savedPost.getPostImages()).hasSize(2);
        assertThat(savedPost.getPostImages())
            .allSatisfy(postImage -> assertThat(postImage.getPost()).isEqualTo(savedPost));
    }

    @Test
    @DisplayName("게시글 생성 시 댓글수는 0 이어야 한다.")
    void createPost_commentCountIsZero() {
        // given
        ArgumentCaptor<CommentCount> captor = ArgumentCaptor.forClass(CommentCount.class);
        PostCreateRequest request = PostRequestFixture.create();
        User author = UserFixture.savedUser(1L);

        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post post = invocation.getArgument(0, Post.class);
            post.assignId(1L);
            return post;
        });
        given(commentCountRepository.save(any(CommentCount.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        PostDetailResponse response = postService.createPostAndImage(1L, request, List.of());

        // then
        then(commentCountRepository).should().save(captor.capture());
        CommentCount savedCommentCount = captor.getValue();

        assertThat(savedCommentCount.getCount()).isEqualTo(0);
        assertThat(savedCommentCount.getPostId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 게시글을 작성할 수 없다.")
    void createPost_whenUserNotFound() {
        // given
        PostCreateRequest request = PostRequestFixture.create();

        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> postService.createPostAndImage(1L, request, null))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", USER_NOT_FOUND);

        then(userRepository).should().findById(1L);

        then(postRepository).should(never()).save(any());
        then(postImageRepository).should(never()).saveAll(anyList());
        then(commentCountRepository).should(never()).save(any());
        then(imageUploader).should(never()).uploadImages(anyList());
        then(postLikeCountCache).should(never()).initCache(anyLong());
        then(postViewCountCache).should(never()).initCache(anyLong());
    }

    @Test
    @DisplayName("이미지 업로드 중 예외가 발생하면 게시글이 저장되지 않는다.")
    void createPost_whenImageUploadFail() {
        // given
        PostCreateRequest request = PostRequestFixture.create();
        List<MultipartFile> images = List.of(mock(MultipartFile.class));
        User author = UserFixture.savedUser(1L);

        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post post = invocation.getArgument(0, Post.class);
            post.assignId(1L);
            return post;
        });
        given(imageUploader.uploadImages(anyList())).willThrow(new CustomException(GCS_IMAGE_UPLOAD_ERROR));

        // when // then
        assertThatThrownBy(() -> postService.createPostAndImage(1L, request, images))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", GCS_IMAGE_UPLOAD_ERROR);

        then(userRepository).should().findById(1L);
        then(imageUploader).should().uploadImages(anyList());

        then(postImageRepository).should(never()).saveAll(anyList());
        then(postLikeCountCache).should(never()).initCache(anyLong());
        then(postViewCountCache).should(never()).initCache(anyLong());
    }
}
