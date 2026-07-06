package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.dto.address.AddressRequest;
import com.mall.service.app.AddressService;
import com.mall.vo.address.AddressResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody AddressRequest request) {
        addressService.add(request);
        return Result.success("新增地址成功", null);
    }

    @GetMapping("/list")
    public Result<List<AddressResponse>> list() {
        return Result.success(addressService.list());
    }

    @GetMapping("/{id}")
    public Result<AddressResponse> detail(@PathVariable Long id) {
        return Result.success(addressService.detail(id));
    }

    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody AddressRequest request) {
        addressService.update(request);
        return Result.success("更新地址成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.success("删除地址成功", null);
    }

    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return Result.success("设置默认地址成功", null);
    }
}
