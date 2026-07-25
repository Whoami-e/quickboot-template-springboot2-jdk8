package com.quickboot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickboot.application.service.UserService;
import com.quickboot.common.api.ErrorCode;
import com.quickboot.common.api.PageResponse;
import com.quickboot.domain.model.User;
import com.quickboot.web.config.JacksonConfig;
import com.quickboot.web.dto.CreateUserRequest;
import com.quickboot.web.dto.UpdateUserRequest;
import com.quickboot.web.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link UserController} Web 层切片测试。
 *
 * <p>使用 {@code @WebMvcTest} 仅加载 Web 层组件（Controller + JacksonConfig + 异常处理器），
 * 通过 {@code @MockBean} 隔离 Service 层，使用 MockMvc 验证所有用户管理接口的 HTTP 响应结构。
 *
 * <p>验证要点：
 * <ul>
 *   <li>POST   /api/users      创建用户正常响应</li>
 *   <li>GET    /api/users/{id} 查询用户正常响应</li>
 *   <li>PUT    /api/users/{id} 更新用户正常响应</li>
 *   <li>DELETE /api/users/{id} 删除用户正常响应</li>
 *   <li>GET    /api/users      分页查询用户列表正常响应</li>
 * </ul>
 */
@WebMvcTest(UserController.class)
@Import({JacksonConfig.class, GlobalExceptionHandler.class})
@DisplayName("UserController 用户管理接口测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    /** 构建测试用固定时间戳 */
    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

    /**
     * 构建标准测试用户对象。
     */
    private User buildTestUser() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .nickname("测试用户")
                .status(1)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    @Test
    @DisplayName("POST /api/users 创建用户返回 HTTP 200 及完整用户信息")
    void createUserReturnsSuccess() throws Exception {
        User testUser = buildTestUser();
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setNickname("测试用户");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    @DisplayName("GET /api/users/{id} 查询用户返回 HTTP 200 及用户信息")
    void getUserByIdReturnsSuccess() throws Exception {
        User testUser = buildTestUser();
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} 更新用户返回 HTTP 200 及更新后信息")
    void updateUserReturnsSuccess() throws Exception {
        User updatedUser = buildTestUser();
        updatedUser.setNickname("新昵称");
        updatedUser.setEmail("new@example.com");
        when(userService.updateUser(eq(1L), any(), any())).thenReturn(updatedUser);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("新昵称");
        request.setEmail("new@example.com");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickname").value("新昵称"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} 删除用户返回 HTTP 200 且 data 为 null")
    void deleteUserReturnsSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/users 分页查询返回 HTTP 200 及分页结构")
    void listUsersReturnsSuccess() throws Exception {
        User user1 = buildTestUser();
        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .nickname("用户二")
                .status(1)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
        PageResponse<User> pageResponse = PageResponse.of(1, 10, 2, Arrays.asList(user1, user2));
        when(userService.listUsers(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/users").param("current", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0].username").value("testuser"))
                .andExpect(jsonPath("$.data.records[1].username").value("user2"));
    }
}
