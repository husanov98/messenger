package uz.mh.messenger.dto;

import lombok.*;
import uz.mh.messenger.enums.StatementStatus;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatementDto {
    private Long id;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String statementId;
    private String text;
    private Integer sentCount;
    private Integer groupCount;
    private StatementStatus status = StatementStatus.ACTUAL;
    private String phoneNumber;
}
