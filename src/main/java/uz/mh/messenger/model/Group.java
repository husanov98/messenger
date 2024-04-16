package uz.mh.messenger.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Group {
    private String chatName;
    private Long chatId;
}
