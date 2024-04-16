package uz.mh.messenger.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Template {
    private String db;
    private List<TgmData> data;
}
