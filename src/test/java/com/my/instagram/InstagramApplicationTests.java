package com.my.instagram;


import com.my.instagram.domains.accounts.dto.request.AccountsSaveRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.service.AccountsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class InstagramApplicationTests {

    @Autowired
    private AccountsService accountsService;

    @Test
    @Rollback(false)
    void 회원입력() {
        for (int i = 0; i < 100; i++) {
            AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
            accountsSaveRequest.setUsername("test"+i+"@gmail.com");
            accountsSaveRequest.setPassword("1234");
            accountsSaveRequest.setProfileName("test"+i);
            accountsSaveRequest.setName("kim"+i);

            accountsService.signUp(accountsSaveRequest);
        }
    }

    @Test
    @Rollback(false)
    void 프로필입력() throws IOException {
        for (int i = 0; i < 1; i++) {
            ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
            int imgNumber = i % 8;
            String filePath = "C:/Images/"+"test"+imgNumber+".jpg";
            byte[] fileData = Files.readAllBytes(Paths.get(filePath));

            MockMultipartFile file = new MockMultipartFile(
                    "test"+imgNumber,
                    "test"+imgNumber+".jpg",
                    "image/jpeg",
                    fileData
            );

            accountsService.updateProfile("test"+i, profileUpdateRequest, file);
        }
    }


}