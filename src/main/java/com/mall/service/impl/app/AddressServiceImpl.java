package com.mall.service.impl.app;

import com.mall.common.exception.BusinessException;
import com.mall.common.util.RequestContext;
import com.mall.dto.address.AddressRequest;
import com.mall.entity.Address;
import com.mall.mapper.AddressMapper;
import com.mall.service.app.AddressService;
import com.mall.vo.address.AddressResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public void add(AddressRequest request) {
        Long userId = currentUserId();
        if (request.getIsDefault() == null) {
            request.setIsDefault(0);
        }
        if (request.getIsDefault() == 1 || addressMapper.selectDefaultByUserId(userId) == null) {
            addressMapper.clearDefault(userId);
        }
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(request.getIsDefault() == 1 ? 1 : 0);
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void update(AddressRequest request) {
        Long userId = currentUserId();
        if (request.getId() == null) {
            throw new BusinessException("地址 ID 不能为空");
        }
        Address existing = addressMapper.selectByIdAndUserId(request.getId(), userId);
        if (existing == null) {
            throw new BusinessException("地址不存在");
        }
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        Address address = new Address();
        address.setId(request.getId());
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(request.getIsDefault() == null ? existing.getIsDefault() : (request.getIsDefault() == 1 ? 1 : 0));
        if (addressMapper.updateByIdAndUserId(address) == 0) {
            throw new BusinessException("更新地址失败");
        }
    }

    @Override
    public void delete(Long id) {
        Long userId = currentUserId();
        if (addressMapper.deleteByIdAndUserId(id, userId) == 0) {
            throw new BusinessException("地址不存在");
        }
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        Long userId = currentUserId();
        if (addressMapper.selectByIdAndUserId(id, userId) == null) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.clearDefault(userId);
        if (addressMapper.setDefault(id, userId) == 0) {
            throw new BusinessException("设置默认地址失败");
        }
    }

    @Override
    public List<AddressResponse> list() {
        return addressMapper.selectListByUserId(currentUserId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AddressResponse detail(Long id) {
        Address address = addressMapper.selectByIdAndUserId(id, currentUserId());
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return toResponse(address);
    }

    private AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUserId());
        response.setReceiverName(address.getReceiverName());
        response.setReceiverPhone(address.getReceiverPhone());
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetailAddress(address.getDetailAddress());
        response.setIsDefault(address.getIsDefault());
        response.setCreateTime(address.getCreateTime());
        response.setUpdateTime(address.getUpdateTime());
        return response;
    }

    private Long currentUserId() {
        Long userId = RequestContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return userId;
    }
}
