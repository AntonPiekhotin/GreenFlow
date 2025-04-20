package org.greenflow.common.model.constant;

/**
 * RabbitMQ constants used in the application.
 * This class contains the names of queues and exchanges used for RabbitMQ messaging.
 */
public class RabbitMQConstants {

    public static final String ORDER_OPENING_QUEUE = "order.opening.queue";
    public static final String ORDER_DELETION_QUEUE = "order.deletion.queue";
    public static final String ORDER_ASSIGNED_QUEUE = "order.assigned.queue";

    public static final String ORDER_EXCHANGE = "order.exchange";

    private RabbitMQConstants() {
    }
}
