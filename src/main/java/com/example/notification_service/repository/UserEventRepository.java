package com.example.notification_service.repository;

import com.example.notification_service.entity.UserEvent;
import com.example.notification_service.entity.UserEventPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, UserEventPK> {
    List<UserEvent> findByUserId(Long userId);
}
