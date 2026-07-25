package com.quickboot.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickboot.common.api.BasePageRequest;
import com.quickboot.common.api.ErrorCode;
import com.quickboot.common.api.PageResponse;
import com.quickboot.common.exception.BusinessException;
import com.quickboot.domain.model.User;
import com.quickboot.infrastructure.entity.UserEntity;
import com.quickboot.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务。
 *
 * <p>编排用户管理的核心业务用例，包括创建、查询、更新、删除和分页查询。
 * 负责 {@link UserEntity}（基础设施层）与 {@link User}（领域层）之间的对象转换。
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 创建用户。
     *
     * <p>校验用户名唯一性后，将领域模型持久化为数据库实体，并返回完整领域对象。
     *
     * @param user 待创建的用户领域模型（不含 id，由雪花算法自动生成）
     * @return 已持久化的用户领域模型（含 id 和时间戳）
     * @throws BusinessException 当用户名已存在时抛出 {@link ErrorCode#USER_ALREADY_EXISTS}
     */
    public User createUser(User user) {
        // 校验用户名唯一性
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, user.getUsername());
        Long count = userMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 构建实体并持久化
        UserEntity entity = toEntity(user);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        entity.setVersion(0);
        userMapper.insert(entity);

        // 返回完整领域对象
        return toDomain(entity);
    }

    /**
     * 根据 ID 查询用户。
     *
     * @param id 用户唯一标识
     * @return 用户领域模型
     * @throws BusinessException 当用户不存在时抛出 {@link ErrorCode#USER_NOT_FOUND}
     */
    public User getUserById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return toDomain(entity);
    }

    /**
     * 更新用户信息。
     *
     * <p>仅更新非空字段（邮箱和昵称），同时刷新 {@code updatedAt} 时间戳。
     *
     * @param id       用户唯一标识
     * @param nickname 新昵称（可为 null 表示不更新）
     * @param email    新邮箱（可为 null 表示不更新）
     * @return 更新后的用户领域模型
     * @throws BusinessException 当用户不存在时抛出 {@link ErrorCode#USER_NOT_FOUND}
     */
    public User updateUser(Long id, String nickname, String email) {
        UserEntity entity = userMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 仅更新非空字段
        if (nickname != null) {
            entity.setNickname(nickname);
        }
        if (email != null) {
            entity.setEmail(email);
        }
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);

        return toDomain(entity);
    }

    /**
     * 删除用户（逻辑删除）。
     *
     * <p>通过 MyBatis-Plus 的 {@code @TableLogic} 实现逻辑删除，数据库中记录不会被物理移除。
     *
     * @param id 用户唯一标识
     * @throws BusinessException 当用户不存在时抛出 {@link ErrorCode#USER_NOT_FOUND}
     */
    public void deleteUser(Long id) {
        UserEntity entity = userMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // MyBatis-Plus @TableLogic 自动转为 UPDATE 逻辑删除
        userMapper.deleteById(id);
    }

    /**
     * 分页查询用户列表。
     *
     * @param request 分页请求参数（页码、每页大小）
     * @return 包含用户列表及分页元信息的响应对象
     */
    public PageResponse<User> listUsers(BasePageRequest<User> request) {
        // 构建实体分页对象（BasePageRequest<User> 的 toPage() 泛型不匹配 UserEntity，故手动构造）
        Page<UserEntity> page = new Page<>(request.getCurrent(), request.getSize());
        page = userMapper.selectPage(page, new LambdaQueryWrapper<>());
        // 将分页结果中的实体批量转换为领域模型
        List<User> records = page.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    /**
     * 将持久化实体转换为领域模型。
     *
     * @param entity 数据库实体
     * @return 领域模型
     */
    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * 将领域模型转换为持久化实体。
     *
     * @param user 领域模型
     * @return 数据库实体（不含 id，由 MyBatis-Plus ASSIGN_ID 策略自动生成）
     */
    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .build();
    }
}
