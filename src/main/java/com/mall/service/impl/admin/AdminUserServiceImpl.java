package com.mall.service.impl.admin;

import com.mall.common.api.PageResult;
import com.mall.common.exception.BusinessException;
import com.mall.entity.User;
import com.mall.mapper.UserMapper;
import com.mall.service.admin.AdminUserService;
import com.mall.vo.admin.user.AdminUserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public PageResult<AdminUserResponse> page(String keyword, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        long total = userMapper.countAdmin(keyword);
        List<User> users = userMapper.selectAdminPage(keyword, (validPageNum - 1) * validPageSize, validPageSize);
        List<AdminUserResponse> list = users.stream().map(this::toResponse).toList();
        return new PageResult<>(total, validPageNum, validPageSize, list);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateStatus(id, status);
    }

    private AdminUserResponse toResponse(User user) {
        AdminUserResponse response = new AdminUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 50);
    }
}
