package egovframework.com.sec.gmt.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import egovframework.com.sec.gmt.service.AuthorGroupInfoVO;
import egovframework.com.sec.gmt.service.EgovAuthorGroupInfoService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class EgovAuthorGroupInfoServiceImplTest {

	@Autowired
	EgovAuthorGroupInfoService egovAuthorGroupInfoService;

	@Test
	void insert() {
		// given
		AuthorGroupInfoVO authorGroupInfoVO = new AuthorGroupInfoVO();
		LocalDateTime now = LocalDateTime.now();
		authorGroupInfoVO.setGroupNm("test 이백행 그룹명 " + now);
		authorGroupInfoVO.setGroupDc("test 이백행 그룹설명 " + now);

		// when
		AuthorGroupInfoVO insertResult = egovAuthorGroupInfoService.insert(authorGroupInfoVO);

		// then
		assertThat(insertResult).isNotNull();
		assertThat(insertResult.getGroupId()).isNotBlank();
		assertThat(insertResult.getGroupNm()).isEqualTo(authorGroupInfoVO.getGroupNm());
		assertThat(insertResult.getGroupCreatDe()).isNotBlank();
		assertThat(insertResult.getGroupDc()).isEqualTo(authorGroupInfoVO.getGroupDc());
		log.debug("insertResult, authorGroupInfoVO");
		log.debug("getGroupId={}, {}", insertResult.getGroupId(), authorGroupInfoVO.getGroupId());
		log.debug("getGroupNm={}, {}", insertResult.getGroupNm(), authorGroupInfoVO.getGroupNm());
		log.debug("getGroupCreatDe={}, {}", insertResult.getGroupCreatDe(), authorGroupInfoVO.getGroupCreatDe());
		log.debug("getGroupDc={}, {}", insertResult.getGroupDc(), authorGroupInfoVO.getGroupDc());

		AuthorGroupInfoVO detailResult = egovAuthorGroupInfoService.detail(insertResult);
		assertThat(detailResult).isNotNull();
		assertThat(detailResult.getGroupId()).isEqualTo(insertResult.getGroupId());
		assertThat(detailResult.getGroupNm()).isEqualTo(insertResult.getGroupNm());
		assertThat(detailResult.getGroupCreatDe()).isEqualTo(insertResult.getGroupCreatDe());
		assertThat(detailResult.getGroupDc()).isEqualTo(insertResult.getGroupDc());
		log.debug("detailResult, insertResult");
		log.debug("getGroupId={}, {}", detailResult.getGroupId(), insertResult.getGroupId());
		log.debug("getGroupNm={}, {}", detailResult.getGroupNm(), insertResult.getGroupNm());
		log.debug("getGroupCreatDe={}, {}", detailResult.getGroupCreatDe(), insertResult.getGroupCreatDe());
		log.debug("getGroupDc={}, {}", detailResult.getGroupDc(), insertResult.getGroupDc());
	}

}
