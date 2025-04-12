package faang.school.postservice.controller.notification;

import faang.school.postservice.notification.PostNotification;
import faang.school.postservice.service.noification_publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationPublisher notificationPublisher;

    @PostMapping("/publish")
    public ResponseEntity<String> publishNotification(@RequestBody PostNotification notification) {
        notificationPublisher.publish(notification);
        return ResponseEntity.ok("Notification published successfully");
    }
}