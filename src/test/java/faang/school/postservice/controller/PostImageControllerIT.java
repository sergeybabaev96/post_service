package faang.school.postservice.controller;

import faang.school.postservice.AbstractIntegrationTest;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.GlobalExceptionHandler;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.adapter.ResourceRepositoryAdapter;
import faang.school.postservice.service.MinioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static faang.school.postservice.PostImageConstantsIT.ANOTHER_AUTHOR_ID_2;
import static faang.school.postservice.PostImageConstantsIT.ANOTHER_POST;
import static faang.school.postservice.PostImageConstantsIT.ANOTHER_POST_2;
import static faang.school.postservice.PostImageConstantsIT.AUTHOR_ID;
import static faang.school.postservice.PostImageConstantsIT.ELEVEN_IMAGES;
import static faang.school.postservice.PostImageConstantsIT.EXISTENT_POST;
import static faang.school.postservice.PostImageConstantsIT.IMAGES_FOR_POST;
import static faang.school.postservice.PostImageConstantsIT.IMAGE_EXCEED_SIZE;
import static faang.school.postservice.PostImageConstantsIT.INVALID_CONTENT;
import static faang.school.postservice.PostImageConstantsIT.NON_EXISTENT_POST_ID;
import static faang.school.postservice.PostImageConstantsIT.NON_EXISTENT_RESOURCE_ID;
import static faang.school.postservice.PostImageConstantsIT.PROJECT_ID;
import static faang.school.postservice.PostImageConstantsIT.RESOURCE_ID;
import static faang.school.postservice.PostImageConstantsIT.TEST_IMAGE_BYTES;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostImageControllerIT extends AbstractIntegrationTest {

    @Autowired
    private PostImageController postImageController;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ResourceRepositoryAdapter resourceRepositoryAdapter;

    @Autowired
    private UserContext userContext;

    @MockBean
    private MinioService minioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postImageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        userContext.clear();
    }

    @Test
    void testAddingImagesToNonExistPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts/images/post/{postId}", NON_EXISTENT_POST_ID)
                        .file(IMAGES_FOR_POST.get(0))
                        .file(IMAGES_FOR_POST.get(1))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Post with ID " + NON_EXISTENT_POST_ID + " not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testAddedImagesNonAuthorPost() throws Exception {
        long postId = setRequesterIdAndSavePost(PROJECT_ID, EXISTENT_POST);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts/images/post/{postId}", postId)
                        .file(IMAGES_FOR_POST.get(0))
                        .file(IMAGES_FOR_POST.get(1))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Adding files by post can only an author post"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUploadingMoreThanTenImages() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        var requestBuilder =
                (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                        .multipart("/api/v1/posts/images/post/{postId}", postId)
                        .contentType(MediaType.MULTIPART_FORM_DATA);

        ELEVEN_IMAGES.forEach(requestBuilder::file);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("You can add at least 1 and no more than 10 photos to a post"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUploadImageExceedsMaxSize() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts/images/post/{postId}", postId)
                        .file(IMAGE_EXCEED_SIZE.get(0))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("The image size exceeds the maximum limit of 5 MB"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUploadNotValidContent() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts/images/post/{postId}", postId)
                        .file(INVALID_CONTENT.get(0))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Uploading allowed only for images"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testSuccessAddImagesToAPost() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        var requestBuilder =
                (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
                        .multipart("/api/v1/posts/images/post/{postId}", postId)
                        .contentType(MediaType.MULTIPART_FORM_DATA);

        IMAGES_FOR_POST.forEach(requestBuilder::file);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Uploading images: " + IMAGES_FOR_POST.size()));
    }

    @Test
    void deleteImagesFromNonExistentPost() throws Exception {
        userContext.setRequesterId(AUTHOR_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/images/{resourceId}/post/{postId}",
                        NON_EXISTENT_RESOURCE_ID, NON_EXISTENT_POST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Post with ID " + NON_EXISTENT_POST_ID + " not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testDeleteImagesNonAuthorPost() throws Exception {
        long postId = setRequesterIdAndSavePost(PROJECT_ID, EXISTENT_POST);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/images/{resourceId}/post/{postId}",
                        RESOURCE_ID, postId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Adding files by post can only an author post"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testDeleteNonExistentResource() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/images/{resourceId}/post/{postId}",
                        RESOURCE_ID, postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testDeleteResourceFromAlienPost() throws Exception {
        postRepository.save(ANOTHER_POST);
        long alienPostId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);

        Resource resource = Resource.builder().key("TEST_KEY").name("TEST_NAME").post(ANOTHER_POST).build();
        resourceRepositoryAdapter.save(resource);

        doNothing().when(minioService).deleteFile(resource.getKey(), "post-bucket");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/images/{resourceId}/post/{postId}",
                        resource.getId(), alienPostId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("This resource belongs to another post"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testSuccessDeleteImageFromAPost() throws Exception {
        long postId = setRequesterIdAndSavePost(AUTHOR_ID, EXISTENT_POST);
        Resource resource = Resource.builder().key("TEST_KEY").name("TEST_NAME").post(EXISTENT_POST).build();
        resourceRepositoryAdapter.save(resource);

        doNothing().when(minioService).deleteFile(resource.getKey(), "post-bucket");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/images/{resourceId}/post/{postId}",
                        resource.getId(), postId))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleting image " + resource.getId() + " successfully ended"));
    }

    @Test
    void testGetNonExistentResource() throws Exception {
        userContext.setRequesterId(AUTHOR_ID);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/images/{resourceId}",
                        RESOURCE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testSuccessGetImageById() throws Exception {
        setRequesterIdAndSavePost(ANOTHER_AUTHOR_ID_2, ANOTHER_POST_2);
        Resource resource = Resource.builder().key("TEST_RESOURCE").name("NAME RESOURCE").post(ANOTHER_POST_2).build();
        resourceRepositoryAdapter.save(resource);

        when(minioService.getFile(resource.getKey(), "post-bucket"))
                .thenReturn(new ByteArrayInputStream(TEST_IMAGE_BYTES));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/images/{resourceId}",
                        resource.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(TEST_IMAGE_BYTES));
    }

    private long setRequesterIdAndSavePost(long userId, Post post) {
        userContext.setRequesterId(userId);
        return postRepository.save(post).getId();
    }
}
