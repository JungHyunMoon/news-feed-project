package F12.newsfeedproject.api.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import F12.newsfeedproject.api.user.dto.request.UserModifyRequestDTO;
import F12.newsfeedproject.api.user.dto.request.UserSignupRequestDTO;
import F12.newsfeedproject.api.user.dto.response.UserResponseDTO;
import F12.newsfeedproject.api.user.service.ApiUserService;
import F12.newsfeedproject.domain.user.constant.UserRole;
import F12.newsfeedproject.global.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(ApiUserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ApiUserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ApiUserController apiUserController;

    @Mock
    private ApiUserService apiUserService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(apiUserController).build();
    }

    @Test
    @DisplayName("UserController : 회원가입 테스트")
    void test1() throws Exception {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // given
        UserSignupRequestDTO userSignupRequestDTO = new UserSignupRequestDTO("testUser", "password",
            "email@email.com", "imageurl.com", "userintroduce");
        UserResponseDTO userResponseDTO = UserResponseDTO.from(
            userSignupRequestDTO.toEntity(passwordEncoder));

        // when
        when(apiUserService.signupUser(userSignupRequestDTO)).thenReturn(userResponseDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(userSignupRequestDTO);

        // ?
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                    .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                    .content(requestJson)
            )
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("UserController : 회원 정보 수정 테스트")
    @WithMockUser(username = "testUser", password = "password", roles = "USER")
    void test2() throws Exception {

        // given
        UserModifyRequestDTO userModifyRequestDTO = new UserModifyRequestDTO("modifiedimageUrl.com",
            "modifiedIntroduce");
        UserResponseDTO userModifyResponseDTO = UserResponseDTO.from(
            userModifyRequestDTO.toEntity());

        // when
        when(apiUserService.updateUser(any(UserModifyRequestDTO.class), anyLong())).thenReturn(
            userModifyResponseDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(userModifyRequestDTO);

        // then
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/users/{userId}", 1L)
                    .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                    .content(requestJson)
            )
            .andExpect(status().isOk())
            .andDo(print());
    }

//    @Test
//    @DisplayName("UserController : 토큰 재발급 테스트")
//    void test3() {
//        String refreshToken = "refreshToken";
//        String accessToken = "accessToken";
//
//        MockHttpServletRequest req = new MockHttpServletRequest();
//        req.addHeader("Authorization", "Bearer" + refreshToken);
//
//        MockHttpServletResponse rep = new MockHttpServletResponse();
//
//        // when
//        when(apiUserService.reissueAccessToken(refreshToken)).thenReturn(accessToken);
//
//        // then
//        ResponseEntity<?> result = apiUserController.reissueAccessToken(req, rep);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("요청 성공", result.getBody());
//        assertEquals(accessToken, rep.getHeader("Access-Token"));
//
//    }
}