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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户应用服务。
 *
 * <p>编排用户管理的核心业务用例，包括创建、查询、更新、删除和分页查询。
 * 支持两种运行模式：</p>
 * <ul>
 *   <li><b>数据库模式</b>：当 MyBatis-Plus 已启用（{@code quickboot.mybatis-plus.enabled=true}）
 *       且 {@link UserMapper} 可用时，通过 Mapper 进行持久化操作。</li>
 *   <li><b>内存模式</b>：当 MyBatis-Plus 未启用或无数据库环境时，使用
 *       {@link ConcurrentHashMap} 模拟 CRUD，保证服务可正常启动和演示。</li>
 * </ul>
 *
 * <p>负责 {@link UserEntity}（基础设施层）与 {@link User}（领域层）之间的对象转换。
 */
@Service
public class UserService {

    /** MyBatis-Plus Mapper，未启用时为 null，自动回退到内存模式 */
    @Autowired(required = false)
    private UserMapper userMapper;

    /** 内存模式：用户数据存储（userId -> User） */
    private final Map<Long, User> memoryStore = new ConcurrentHashMap<>();

    /** 内存模式：自增 ID 生成器 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 创建用户。
     *
     * <p>校验用户名唯一性后持久化或存入内存，并返回完整领域对象。
     *
     * @param user 待创建的用户领域模型（数据库模式下不含 id，由雪花算法自动生成）
     * @return 已创建的用户领域模型（含 id 和时间戳）
     * @throws BusinessException 当用户名已存在时抛出 {@link ErrorCode#USER_ALREADY_EXISTS}
     */
    public User createUser(User user) {
        if (userMapper != null) {
            return createUserFromDb(user);
        }
        return createUserFromMemory(user);
    }

    /**
     * 根据 ID 查询用户。
     *
     * @param id 用户唯一标识
     * @return 用户领域模型
     * @throws BusinessException 当用户不存在时抛出 {@link ErrorCode#USER_NOT_FOUND}
     */
    public User getUserById(Long id) {
        if (userMapper != null) {
            UserEntity entity = userMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return toDomain(entity);
        }
        // 内存模式
        User user = memoryStore.get(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
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
        if (userMapper != null) {
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
        // 内存模式
        User user = memoryStore.get(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (email != null) {
            user.setEmail(email);
        }
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * 删除用户（数据库模式为逻辑删除，内存模式为直接移除）。
     *
     * <p>数据库模式下通过 MyBatis-Plus 的 {@code @TableLogic} 实现逻辑删除，
     * 数据库中记录不会被物理移除。
     *
     * @param id 用户唯一标识
     * @throws BusinessException 当用户不存在时抛出 {@link ErrorCode#USER_NOT_FOUND}
     */
    public void deleteUser(Long id) {
        if (userMapper != null) {
            UserEntity entity = userMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            // MyBatis-Plus @TableLogic 自动转为 UPDATE 逻辑删除
            userMapper.deleteById(id);
            return;
        }
        // 内存模式
        User removed = memoryStore.remove(id);
        if (removed == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * 分页查询用户列表。
     *
     * @param request 分页请求参数（页码、每页大小）
     * @return 包含用户列表及分页元信息的响应对象
     */
    public PageResponse<User> listUsers(BasePageRequest<User> request) {
        if (userMapper != null) {
            return listUsersFromDb(request);
        }
        return listUsersFromMemory(request);
    }

    // ==================== 数据库模式实现 ====================

    /**
     * 数据库模式：创建用户。
     */
    private User createUserFromDb(User user) {
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

        return toDomain(entity);
    }

    /**
     * 数据库模式：分页查询用户列表。
     */
    private PageResponse<User> listUsersFromDb(BasePageRequest<User> request) {
        Page<UserEntity> page = new Page<>(request.getCurrent(), request.getSize());
        page = userMapper.selectPage(page, new LambdaQueryWrapper<>());
        List<User> records = page.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    // ==================== 内存模式实现 ====================

    /**
     * 内存模式：创建用户。
     */
    private User createUserFromMemory(User user) {
        // 校验用户名唯一性
        boolean exists = memoryStore.values().stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername()));
        if (exists) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 分配 ID 并设置时间戳
        LocalDateTime now = LocalDateTime.now();
        User created = User.builder()
                .id(idGenerator.getAndIncrement())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .createdAt(now)
                .updatedAt(now)
                .build();
        memoryStore.put(created.getId(), created);
        return created;
    }

    /**
     * 内存模式：分页查询用户列表。
     */
    private PageResponse<User> listUsersFromMemory(BasePageRequest<User> request) {
        List<User> allUsers = new ArrayList<>(memoryStore.values());
        int total = allUsers.size();
        int fromIndex = (request.getCurrent() - 1) * request.getSize();
        int toIndex = Math.min(fromIndex + request.getSize(), total);

        // 边界处理：起始索引超出范围时返回空列表
        List<User> records = fromIndex >= total
                ? new ArrayList<>()
                : allUsers.subList(fromIndex, toIndex);

        return PageResponse.of((long) request.getCurrent(), (long) request.getSize(),
                (long) total, records);
    }

    // ==================== 对象转换 ====================

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
