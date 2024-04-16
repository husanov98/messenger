package uz.mh.messenger.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Session {
    private String phoneNumber;
    private String db;
    private Long chatId;
}
