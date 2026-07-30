package egovframework.com.mip.mva.sp.comm.util;

import egovframework.com.mip.mva.sp.comm.enums.MipErrorEnum;
import egovframework.com.mip.mva.sp.comm.exception.SpException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.util
 * @FileName HttpUtil.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description Http Call Util
 *
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * 2026. 7. 24.    EricSeokgon      RestTemplate connect/read 타임아웃 설정 (무응답 무한 블록 방지, CWE-400)
 * </pre>
 */
public class HttpUtil {

	// 안정성: 기본 RestTemplate 은 connect/read 타임아웃이 없어(무한 대기) 외부 서버가
	// 무응답이면 호출 스레드가 영구히 블록될 수 있다(CWE-400 자원 고갈).
	// 타임아웃을 설정한 요청 팩토리로 RestTemplate 을 구성해 재사용한다.
	private static final int CONNECT_TIMEOUT_MS = 5000;
	private static final int READ_TIMEOUT_MS = 30000;
	private static final RestTemplate REST_TEMPLATE = createRestTemplate();

	private static RestTemplate createRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
		factory.setReadTimeout(READ_TIMEOUT_MS);
		return new RestTemplate(factory);
	}

	/**
	 * Http Call(POST) 실행
	 *
	 * @MethodName executeHttpPost
	 * @param url URL
	 * @param param 파라미터
	 * @return 결과
	 * @throws SpException
	 */
	public static String executeHttpPost(String url, Object param) throws SpException {
		ResponseEntity<String> response = null;

		try {
			response = REST_TEMPLATE.postForEntity(url, param, String.class);
		} catch (RestClientException e) {
			throw new SpException(MipErrorEnum.SP_NETWORK_ERROR);
		}

		return response.getBody();
	}

}
