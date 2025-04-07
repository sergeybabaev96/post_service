package faang.school.postservice.util.album;

import faang.school.postservice.controller.album.AlbumController;
import faang.school.postservice.service.album.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AlbumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlbumServiceImpl albumService;

    @InjectMocks
    private AlbumController albumController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
    }

//    @Test
//    public void testGetUsers() throws Exception {
//        // создаем список ожидаемых пользователей
//        List<User> expectedUsers = Arrays.asList(new User("John"), new User("Jane"));
//
//        // Настройка заглушки сервиса
//        when(service.getUsers()).thenReturn(expectedUsers);
//
//        // Выполнение запроса
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("John")))
//                .andExpect(jsonPath("$[1].name", is("Jane")));
//    }


}
