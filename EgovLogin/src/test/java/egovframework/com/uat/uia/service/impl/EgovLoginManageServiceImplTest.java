package egovframework.com.uat.uia.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import egovframework.com.uat.uia.repository.EgovEmployMemberRepository;
import egovframework.com.uat.uia.service.EgovLoginManageService;
import egovframework.com.uat.uia.service.LoginDTO;
import egovframework.com.uat.uia.service.LoginVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class EgovLoginManageServiceImplTest {

	@Autowired
	EgovLoginManageService egovLoginManageService;

	@Autowired
	EgovEmployMemberRepository egovEmployMemberRepository;

	@Test
	void actionLogin() {
		// given
		LoginVO loginVO = new LoginVO();

		loginVO.setUserSe("USR");
		loginVO.setUserId("TEST1");
		loginVO.setUserPw("rhdxhd12");

//		loginVO.setUserSe("USR");
////		loginVO.setUserId("TEST1");
//		LocalDateTime now = LocalDateTime.now();
//		String now2 = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//		loginVO.setUserId("TEST1_" + now2);
//		loginVO.setUserPw("rhdxhd12");
//
////		Pageable pageable = PageRequest.of(0, 1);
////		Page<EmplyrInfo> results = egovEmployMemberRepository.findAll(pageable);
////		for (EmplyrInfo emplyrInfo : results) {
////			loginVO.setUserSe("USR");
////			loginVO.setUserId(emplyrInfo.getEmplyrId());
////			loginVO.setUserPw("rhdxhd12");
////		}
//
//		EmplyrInfo emplyrInfo = new EmplyrInfo();
//		emplyrInfo.setEmplyrId(loginVO.getUserId());
//		String encPassword = encryptPassword(loginVO.getUserPw(), loginVO.getUserId());
//		emplyrInfo.setPassword(encPassword);
//		emplyrInfo.setAreaNo("02");
//		emplyrInfo.setEmplyrStusCode("P");
//		emplyrInfo.setEsntlId(loginVO.getUserId());
//		emplyrInfo.setHouseAdres("서울 중구 무교동 한국정보화진흥원");
//		emplyrInfo.setHouseEndTelno("2059");
//		emplyrInfo.setHouseMiddleTelno("1566");
//		emplyrInfo.setPasswordCnsr("전자정부표준프레임워크센터");
//		emplyrInfo.setPasswordHint("P01");
//		emplyrInfo.setUserNm("테스트1_" + now2);
//		emplyrInfo.setZip("100775");
//		egovEmployMemberRepository.save(emplyrInfo);

		// when
		LoginDTO loginDTO = egovLoginManageService.actionLogin(loginVO);

		// then
		assertThat(loginDTO).isNotNull();

		assertThat(loginDTO.getUserSe()).isEqualTo(loginVO.getUserSe());
		assertThat(loginDTO.getId()).isEqualTo(loginVO.getUserId());
//		assertThat(loginDTO.getPassword()).isEqualTo(loginVO.getUserPw());

		log.debug("loginDTO, loginVO");
		log.debug("getUserSe={}, {}", loginDTO.getUserSe(), loginVO.getUserSe());
		log.debug("getId={},{}", loginDTO.getId(), loginVO.getUserId());
//		log.debug("getPassword={},{}", loginDTO.getPassword(), loginVO.getUserPw());
	}

//	private String encryptPassword(String key, String salt) {
//		try {
//			MessageDigest md = MessageDigest.getInstance("SHA-256");
//			md.reset();
//			md.update(salt.getBytes(StandardCharsets.UTF_8));
//			return Base64.encodeBase64String(md.digest(key.getBytes(StandardCharsets.UTF_8)));
//		} catch (NoSuchAlgorithmException e) {
//			log.debug("##### EgovLoginManageServiceImpl NoSuchAlgorithmException >>> {}", e.getMessage());
//			return "0";
//		}
//	}

}
