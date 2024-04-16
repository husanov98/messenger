package uz.mh.messenger.model;

import lombok.*;
import uz.mh.messenger.response.MessageResponse;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ApiResponse {

    private MessageResponse content;
    private String error;
    private boolean success = true;
    private int code;

    public ApiResponse(MessageResponse content,String error,boolean success){
        this.content = content;
        this.error = error;
        this.success = success;
    }

}
