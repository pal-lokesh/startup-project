package com.example.RecordService.repository;

import com.example.RecordService.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE c.clientPhone = :clientPhone AND c.vendorPhone = :vendorPhone AND c.businessId = :businessId AND c.isActive = true")
    Optional<Chat> findActiveChat(@Param("clientPhone") String clientPhone, 
                                 @Param("vendorPhone") String vendorPhone, 
                                 @Param("businessId") String businessId);
    
    @Query("SELECT c FROM Chat c WHERE (c.clientPhone = :phoneNumber OR c.vendorPhone = :phoneNumber) AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT c FROM Chat c WHERE c.clientPhone = :clientPhone AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByClient(@Param("clientPhone") String clientPhone);
    
    @Query("SELECT c FROM Chat c WHERE c.vendorPhone = :vendorPhone AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByVendor(@Param("vendorPhone") String vendorPhone);
    
    @Query("SELECT c FROM Chat c WHERE c.businessId = :businessId AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByBusiness(@Param("businessId") String businessId);
    
    @Query("SELECT c FROM Chat c WHERE c.orderId = :orderId AND c.isActive = true")
    Optional<Chat> findChatByOrderId(@Param("orderId") Long orderId);
}
