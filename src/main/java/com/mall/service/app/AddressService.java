package com.mall.service.app;

import com.mall.dto.address.AddressRequest;
import com.mall.vo.address.AddressResponse;
import java.util.List;

public interface AddressService {

    void add(AddressRequest request);

    void update(AddressRequest request);

    void delete(Long id);

    void setDefault(Long id);

    List<AddressResponse> list();

    AddressResponse detail(Long id);
}
