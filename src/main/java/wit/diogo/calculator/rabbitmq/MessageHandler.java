package wit.diogo.calculator.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import wit.diogo.calculator.rabbitmq.dto.MessageDto;

import java.math.BigDecimal;

@Service
public class MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private RabbitTemplate rabbitTemplate;

    public MessageHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.configuration.read-queue.name}")
    @SendTo
    public Message consume(MessageDto message) {
        MDC.put("identifier", message.getIdentifier());

        LOGGER.info("Module -> Calculator - Identifier -> " + MDC.get("identifier") +  " - Performing calculation");

        BigDecimal result = calculateResult(message);

        LOGGER.info("Module -> Calculator - Identifier -> " + MDC.get("identifier") +  " - Calculation completed sending result to rest module");

        //We process null responses on the Rest side
        return rabbitTemplate.getMessageConverter().toMessage(result, null);
    }

    private BigDecimal calculateResult(MessageDto message) {
        try {
            switch (message.getOperation()) {
                case ADD -> {
                    return message.getFirstValue().add(message.getSecondValue());
                }
                case SUB -> {
                    return message.getFirstValue().subtract(message.getSecondValue());
                }
                case MUL -> {
                    return message.getFirstValue().multiply(message.getSecondValue());
                }
                case DIV -> {
                    return message.getFirstValue().divide(message.getSecondValue());
                }
                default -> LOGGER.info("Unknown operation");
            }
        } catch (ArithmeticException e) {
            //this should be LOGGER.error() But for the sake of challenge i will leave it this way
            LOGGER.info(String.format("Problem performing calculation -> %s", e.getMessage()));
        }
        return null;
    }
}
