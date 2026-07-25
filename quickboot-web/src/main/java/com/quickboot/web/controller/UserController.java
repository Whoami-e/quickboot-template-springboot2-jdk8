package com.quickboot.web.controller;

import com.quickboot.application.service.UserService;
import com.quickboot.common.api.ApiResponse;
import com.quickboot.common.api.BasePageRequest;
import com.quickboot.common.api.PageResponse;
import com.quickboot.domain.model.User;
import com.quickboot.web.dto.CreateUserRequest;
import com.quickboot.web.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户管理 REST 控制器。
 *
 * <p>提供用户 CRUD 及分页查询接口，统一使用 {@link ApiResponse} 封装响应，
 * 通过 {@link Validated} 开启请求参数校验。
 *
 * <p>接口列表：
 * <ul>
 *   <li>{@code POST   /api/users}      创建用户</li>
 *   <li>{@code GET    /api/users/{id}} 查询单个用户</li>
 *   <li>{@code PUT    /api/users/{id}} 更新用户</li>
 *   <li>{@code DELETE /api/users/{id}} 删除用户</li>
 *   <li>{@code GET    /api/users}      分页查询用户列表</li>
 * </ul>
 */
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户。
     *
     * @param request 创建用户请求参数（用户名必填，邮箱需符合格式）
     * @return 包含新建用户信息的成功响应
     */
    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        // 将 DTO 转换为领域模型
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .status(1) // 默认启用
                .build();
        return ApiResponse.success(userService.createUser(user));
    }

    /**
     * 根据 ID 查询用户。
     *
     * @param id 用户唯一标识（路径参数）
     * @return 包含用户信息的成功响应
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    /**
     * 更新用户信息。
     *
     * @param id      用户唯一标识（路径参数）
     * @param request 更新用户请求参数（邮箱和昵称均为可选）
     * @return 包含更新后用户信息的成功响应
     */
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.updateUser(id, request.getNickname(), request.getEmail()));
    }

    /**
     * 删除用户（逻辑删除）。
     *
     * @param id 用户唯一标识（路径参数）
     * @return 无数据的成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    /**
     * 分页查询用户列表。
     *
     * @param request 分页请求参数（页码 current，每页大小 size）
     * @return 包含分页用户列表的成功响应
     */
    @GetMapping
    public ApiResponse<PageResponse<User>> listUsers(@Valid BasePageRequest<User> request) {
        return ApiResponse.success(userService.listUsers(request));
    }
}
