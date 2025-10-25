package com.example.RecordService.repository;

import com.example.RecordService.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chat.chatId = :chatId ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesByChatId(@Param("chatId") Long chatId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chat.chatId = :chatId AND cm.isRead = false AND cm.senderPhone != :phoneNumber ORDER BY cm.createdAt ASC")
    List<ChatMessage> findUnreadMessages(@Param("chatId") Long chatId, @Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chat.chatId = :chatId AND cm.isRead = false AND cm.senderPhone != :phoneNumber")
    Long countUnreadMessages(@Param("chatId") Long chatId, @Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chat.chatId = :chatId AND cm.createdAt > :since ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesSince(@Param("chatId") Long chatId, @Param("since") LocalDateTime since);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.senderPhone = :phoneNumber AND cm.isRead = false")
    List<ChatMessage> findUnreadMessagesBySender(@Param("phoneNumber") String phoneNumber);
}
