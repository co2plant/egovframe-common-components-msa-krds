package egovframework.com.cop.bbs.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import egovframework.com.cop.bbs.service.BbsMasterVO;
import egovframework.com.cop.bbs.service.EgovBbsMasterService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class EgovBbsMasterServiceImplTest {

	@Autowired
	EgovBbsMasterService egovBbsMasterService;

	@Test
	void insert() {
		// given
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("uniqId", "USRCNFRM_00000000000");

		BbsMasterVO bbsMasterVO = new BbsMasterVO();
		bbsMasterVO.setAtchPosblFileNumber(0);

		LocalDateTime now = LocalDateTime.now();
		bbsMasterVO.setBbsNm("test 이백행 게시판명 " + now);

		bbsMasterVO.setBbsTyCode("test");

		bbsMasterVO.setFileAtchPosblAt("Y");

		bbsMasterVO.setUseAt("Y");

		// when
		BbsMasterVO result = egovBbsMasterService.insert(bbsMasterVO, userInfo);

		// then
//		log.debug("result={}", result);
		log.debug("getBbsId={}", result.getBbsId());
		log.debug("getFrstRegistPnttm={}", result.getFrstRegistPnttm());
		log.debug("getFrstRegisterId={}", result.getFrstRegisterId());
		log.debug("getLastUpdtPnttm={}", result.getLastUpdtPnttm());
		log.debug("getLastUpdusrId={}", result.getLastUpdusrId());

		assertThat(result).isNotNull();

		if (!ObjectUtils.isEmpty(result)) {
			assertThat(result).isNotNull();
		} else {
			assertThat(result).isNull();
		}
	}

}
