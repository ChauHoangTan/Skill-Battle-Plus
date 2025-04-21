package com.example.notificationservice.Factory;

import com.example.notificationservice.Strategy.EmailNotificationStrategy;
import com.example.notificationservice.Strategy.NotificationStrategy;
import com.example.notificationservice.Strategy.PushNotificationStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationStrategyFactory {
    private final Map<String, NotificationStrategy> strategies = new HashMap<>();

    public NotificationStrategyFactory(List<NotificationStrategy> strategyList) {
        for (NotificationStrategy strategy : strategyList) {
            String key = strategy.getClass().getSimpleName().replace("NotificationStrategy", "").toLowerCase();
            strategies.put(key, strategy);
        }
    }

    public NotificationStrategy getStrategy(String type) {
        NotificationStrategy strategy = strategies.get(type.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported notification type: " + type);
        }
        return strategy;
    }
}
