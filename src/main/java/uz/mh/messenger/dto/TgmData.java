package uz.mh.messenger.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TgmData {
    private String id;
    private int group_count;
    private int count;
}
