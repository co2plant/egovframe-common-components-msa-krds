package egovframework.com.cop.brd.service;

/**
 * 게시판 옵션 조회 API 응답용 — 감사 메타데이터(등록·수정자, 시각) 제외.
 */
public record BbsMasterOptnPublicVO(
        String bbsId,
        String answerAt,
        String stsfdgAt
) {
    public static BbsMasterOptnPublicVO from(BbsMasterOptnVO src) {
        if (src == null) {
            return null;
        }
        return new BbsMasterOptnPublicVO(
                src.getBbsId(),
                src.getAnswerAt(),
                src.getStsfdgAt()
        );
    }
}
