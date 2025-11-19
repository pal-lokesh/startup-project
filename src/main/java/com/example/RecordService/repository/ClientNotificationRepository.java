package com.example.RecordService.repository;

import com.example.RecordService.entity.ClientNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientNotificationRepository extends JpaRepository<ClientNotification, Long> {

    List<ClientNotification> findByClientPhoneOrderByCreatedAtDesc(String clientPhone);

    long countByClientPhoneAndIsReadFalse(String clientPhone);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ClientNotification n SET n.isRead = true WHERE n.clientPhone = :clientPhone AND n.isRead = false")
    void markAllAsReadByClientPhone(@Param("clientPhone") String clientPhone);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE client_notifications SET is_read = true WHERE notification_id = :notificationId", nativeQuery = true)
    int markNotificationAsReadById(@Param("notificationId") Long notificationId);

    List<ClientNotification> findByClientPhoneAndNotificationTypeOrderByCreatedAtDesc(String clientPhone, ClientNotification.NotificationType notificationType);

    /**
     * Find recent notifications for a client (last 30 days)
     * @param clientPhone the client's phone number
     * @return list of recent notifications
     */
    @Query("SELECT n FROM ClientNotification n WHERE n.clientPhone = :clientPhone AND n.createdAt >= :startDate ORDER BY n.createdAt DESC")
    List<ClientNotification> findRecentNotificationsByClient(@Param("clientPhone") String clientPhone, @Param("startDate") java.time.LocalDateTime startDate);
}
