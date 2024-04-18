package uz.mh.messenger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mh.messenger.enums.StatementStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "statements")
public class Statement extends Auditable{
    @Column(unique = true)
    private String statementId;
    private String text;
    private Integer sentCount;
    private Integer groupCount;
    @Enumerated(EnumType.STRING)
    private StatementStatus status = StatementStatus.ACTUAL;
    private String phoneNumber;

}
