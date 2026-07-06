package com.mall.config;

import com.mall.common.util.PasswordUtils;
import com.mall.entity.Admin;
import com.mall.entity.User;
import com.mall.mapper.AdminMapper;
import com.mall.mapper.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;

    @Override
    public void run(String... args) {
        upgradeAdminPasswords();
        upgradeUserPasswords();
    }

    private void upgradeAdminPasswords() {
        List<Admin> admins = adminMapper.selectAll();
        int upgraded = 0;
        for (Admin admin : admins) {
            if (PasswordUtils.isEncoded(admin.getPassword())) {
                continue;
            }
            String encoded = PasswordUtils.encode(admin.getPassword());
            adminMapper.updatePassword(admin.getId(), encoded);
            upgraded++;
        }
        if (upgraded > 0) {
            log.info("DataInitializer: upgraded {} admin password(s) to BCrypt", upgraded);
        }
    }

    private void upgradeUserPasswords() {
        List<User> users = userMapper.selectAll();
        int upgraded = 0;
        for (User user : users) {
            if (PasswordUtils.isEncoded(user.getPassword())) {
                continue;
            }
            String encoded = PasswordUtils.encode(user.getPassword());
            userMapper.updatePassword(user.getId(), encoded);
            upgraded++;
        }
        if (upgraded > 0) {
            log.info("DataInitializer: upgraded {} user password(s) to BCrypt", upgraded);
        }
    }
}
