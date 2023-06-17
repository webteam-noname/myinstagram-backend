package com.my.instagram;

import com.my.instagram.domains.accounts.domain.Accounts;
import com.my.instagram.domains.accounts.dto.request.AccountsSaveRequest;
import com.my.instagram.domains.accounts.dto.request.ProfileUpdateRequest;
import com.my.instagram.domains.accounts.service.AccountsService;
import com.my.instagram.domains.follow.dto.request.FollowSaveRequest;
import com.my.instagram.domains.follow.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class InstagramApplicationTests {

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private FollowService followService;


	@Test
	@Rollback(false)
	void 회원입력() {
		for (int i = 0; i < 100; i++) {
			AccountsSaveRequest accountsSaveRequest = new AccountsSaveRequest();
			accountsSaveRequest.setUsername("test"+i+"@gmail.com");
			accountsSaveRequest.setPassword("1234");
			accountsSaveRequest.setProfileName("test"+i);
			accountsSaveRequest.setName("kim"+i);

			accountsService.join(accountsSaveRequest);
		}
	}

	@Test
	@Rollback(false)
	void 팔로우입력(){
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(i == j){
					continue;
				}
				FollowSaveRequest followSaveRequest = new FollowSaveRequest();
				followSaveRequest.setProfileName("test"+i);
				followSaveRequest.setFollowName("test"+j);
				followService.saveFollow(followSaveRequest);
			}
		}
	}

	@Test
	@Rollback(false)
	void 프로필입력() throws IOException {
		for (int i = 0; i < 100; i++) {
			ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
			profileUpdateRequest.setProfileName("test"+i);
			int imgNumber = i % 8;
			String filePath = "C:/Images/"+"test"+imgNumber+".jpg";
			byte[] fileData = Files.readAllBytes(Paths.get(filePath));

			MockMultipartFile file = new MockMultipartFile(
					"profileImg",
					"test"+i,
					"image/jpeg",
					fileData
			);

			accountsService.updateProfile(profileUpdateRequest, file);
		}
	}


}
