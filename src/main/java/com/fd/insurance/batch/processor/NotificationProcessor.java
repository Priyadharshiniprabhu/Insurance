package com.fd.insurance.batch.processor;

import com.fd.insurance.entity.ReminderOutbox;
import com.fd.insurance.enums.NotificationOutcome;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NotificationProcessor
        implements ItemProcessor<ReminderOutbox, ReminderOutbox> {

    private final Random random = new Random();

    @Override
    public ReminderOutbox process(ReminderOutbox item) {

        int number = random.nextInt(3);

        NotificationOutcome outcome;

        if (number == 0) {
            outcome = NotificationOutcome.SENT;

        } else if (number == 1) {
            outcome = NotificationOutcome.FAILED;

        } else {
            outcome = NotificationOutcome.GATEWAY_TIMEOUT;
        }

        System.out.println(
                "Event "
                        + item.getId()
                        + " -> "
                        + outcome);

        return item;
    }
}