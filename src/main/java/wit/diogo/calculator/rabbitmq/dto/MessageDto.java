package wit.diogo.calculator.rabbitmq.dto;

import lombok.Data;
import wit.diogo.calculator.rabbitmq.enums.Operation;

import java.math.BigDecimal;

@Data
public class MessageDto {
    private BigDecimal firstValue;
    private BigDecimal secondValue;
    private Operation operation;
}
