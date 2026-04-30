package com.eldercare.repository;

import com.eldercare.entity.Conversation;
import com.eldercare.entity.Guardian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대화 레포지토리
 *
 * 대화 세션 저장, 조회, 검색 기능 제공
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

	/**
	 * 특정 참여자의 대화 목록 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian OR c.target = :guardian ORDER BY c.lastMessageAt DESC")
	List<Conversation> findByParticipant(@Param("guardian") Guardian guardian);

	/**
	 * 특정 참여자의 대화 목록 (페이지네이션)
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian OR c.target = :guardian ORDER BY c.lastMessageAt DESC")
	Page<Conversation> findByParticipant(@Param("guardian") Guardian guardian, Pageable pageable);

	/**
	 * 두 사용자 간의 대화 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE (c.participant = :guardian1 AND c.target = :guardian2) OR (c.participant = :guardian2 AND c.target = :guardian1)")
	Optional<Conversation> findBetweenTwoUsers(@Param("guardian1") Guardian guardian1, @Param("guardian2") Guardian guardian2);

	/**
	 * 특정 유형의 대화 조회
	 */
	List<Conversation> findByParticipantAndConversationType(Guardian participant, String conversationType);

	/**
	 * AI와의 대화 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.conversationType = 'USER_TO_AI' ORDER BY c.createdAt DESC")
	List<Conversation> findAIConversations(@Param("guardian") Guardian guardian);

	/**
	 * 활성 대화만 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.status = 'ACTIVE' ORDER BY c.lastMessageAt DESC")
	List<Conversation> findActiveConversations(@Param("guardian") Guardian guardian);

	/**
	 * 종료되지 않은 대화
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.status != 'ENDED' AND c.deletedAt IS NULL ORDER BY c.lastMessageAt DESC")
	List<Conversation> findOngoingConversations(@Param("guardian") Guardian guardian);

	/**
	 * 특정 기간의 대화
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.startedAt BETWEEN :startDate AND :endDate ORDER BY c.startedAt DESC")
	List<Conversation> findByDateRange(@Param("guardian") Guardian guardian,
									   @Param("startDate") LocalDateTime startDate,
									   @Param("endDate") LocalDateTime endDate);

	/**
	 * 감정 점수 기준 대화 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.sentimentScore < :threshold ORDER BY c.sentimentScore ASC")
	List<Conversation> findNegativeSentimentConversations(@Param("guardian") Guardian guardian, @Param("threshold") Double threshold);

	/**
	 * 최신 메시지 기준 정렬
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.deletedAt IS NULL ORDER BY c.lastMessageAt DESC LIMIT :limit")
	List<Conversation> findRecentConversations(@Param("guardian") Guardian guardian, @Param("limit") int limit);

	/**
	 * 고정된 대화 (pinned)
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.pinned = true ORDER BY c.lastMessageAt DESC")
	List<Conversation> findPinnedConversations(@Param("guardian") Guardian guardian);

	/**
	 * 검색 기능
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Conversation> searchConversations(@Param("guardian") Guardian guardian, @Param("keyword") String keyword);

	/**
	 * 주제별 대화
	 */
	List<Conversation> findByParticipantAndTopic(Guardian participant, String topic);

	/**
	 * 보관된 대화 조회
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.isArchived = true ORDER BY c.updatedAt DESC")
	List<Conversation> findArchivedConversations(@Param("guardian") Guardian guardian);

	/**
	 * 대화 통계
	 */
	@Query("SELECT c.conversationType, COUNT(c) as count FROM Conversation c WHERE c.participant = :guardian GROUP BY c.conversationType")
	List<Object[]> getConversationTypeStats(@Param("guardian") Guardian guardian);

	/**
	 * 감정 분석 평균
	 */
	@Query("SELECT AVG(c.sentimentScore) FROM Conversation c WHERE c.participant = :guardian AND c.sentimentScore IS NOT NULL")
	Double getAverageSentimentScore(@Param("guardian") Guardian guardian);

	/**
	 * 소프트 삭제되지 않은 대화
	 */
	@Query("SELECT c FROM Conversation c WHERE c.participant = :guardian AND c.deletedAt IS NULL")
	List<Conversation> findNotDeletedConversations(@Param("guardian") Guardian guardian);
}
