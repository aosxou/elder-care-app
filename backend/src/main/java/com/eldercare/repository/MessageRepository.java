package com.eldercare.repository;

import com.eldercare.entity.Message;
import com.eldercare.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 레포지토리
 *
 * 메시지 저장, 조회, 검색 기능 제공
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

	/**
	 * 특정 대화의 모든 메시지 조회
	 */
	List<Message> findByConversation(Conversation conversation);

	/**
	 * 특정 대화의 읽지 않은 메시지 조회
	 */
	@Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.isRead = false")
	List<Message> findUnreadMessages(@Param("conversation") Conversation conversation);

	/**
	 * 특정 발신자의 메시지 조회
	 */
	@Query("SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.createdAt DESC")
	List<Message> findBySenderId(@Param("senderId") String senderId);

	/**
	 * 특정 기간 내 메시지 검색
	 */
	@Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
	List<Message> findByDateRange(@Param("conversation") Conversation conversation,
								  @Param("startDate") LocalDateTime startDate,
								  @Param("endDate") LocalDateTime endDate);

	/**
	 * 특정 감정의 메시지 조회
	 */
	@Query("SELECT m FROM Message m WHERE m.sentiment = :sentiment AND m.conversation = :conversation")
	List<Message> findBySentiment(@Param("sentiment") String sentiment,
								  @Param("conversation") Conversation conversation);

	/**
	 * 텍스트 검색
	 */
	@Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Message> searchByContent(@Param("conversation") Conversation conversation,
								  @Param("keyword") String keyword);

	/**
	 * 특정 메시지 타입 조회
	 */
	List<Message> findByConversationAndMessageType(Conversation conversation, String messageType);

	/**
	 * 읽음 상태로 표시
	 */
	@Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE m.id = :messageId")
	void markAsRead(@Param("messageId") String messageId, @Param("readAt") LocalDateTime readAt);

	/**
	 * 대화에서 삭제되지 않은 메시지의 개수
	 */
	@Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.deletedAt IS NULL")
	long countByConversationNotDeleted(@Param("conversation") Conversation conversation);

	/**
	 * 감정 분석 결과가 있는 메시지 조회
	 */
	@Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.emotionScore IS NOT NULL ORDER BY m.emotionScore DESC")
	List<Message> findMessagesWithEmotionAnalysis(@Param("conversation") Conversation conversation);
}
