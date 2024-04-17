package uz.mh.messenger.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import uz.mh.messenger.model.ApiResponse;
import uz.mh.messenger.service.TdlightService;


@RestController
//@CrossOrigin(origins = "*")
public class TdlightController {


    private final TdlightService service;

    public TdlightController(TdlightService service) {
        this.service = service;
    }


    @PostMapping(value = "sendPhoneNumber",consumes = {"multipart/form-data"})
    public ResponseEntity<?> send(@RequestPart(name = "phoneNumber") String phoneNumber) throws Exception {
        ApiResponse apiResponse = service.registerwithPhoneNumber(phoneNumber);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
    }

    @PostMapping(value = "sendCode",consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendCode(@RequestPart(name = "phoneNumber") String phoneNumber,
                                      @RequestPart(name = "code") String code) throws Exception {
        ApiResponse apiResponse = service.enterAuthenticationCode(phoneNumber, code);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
    }

//    @PostMapping(value = "xabar",consumes = {"multipart/form-data"})
//    public ResponseEntity<?> message(@RequestPart(name = "phoneNumber") String phone,
//                                     @RequestPart(name = "htmlText") String htmlText,
//                                     @RequestPart(name = "db") String db) throws Exception {
//
//
//        ApiResponse apiResponse = service.sendMessageToGroup(phone, htmlText, db);
//        return new ResponseEntity<>(apiResponse,HttpStatusCode.valueOf(apiResponse.getCode()));
//    }

//    @PostMapping(value = "updateMessage",consumes = {"multipart/form-data"})
//    public ResponseEntity<?> updateMessage(@RequestPart(name = "newText")String newText,
//                                           @RequestPart(name = "chatId") String chatId,
//                                           @RequestPart(name = "updatableMessageId") String messageId,
//                                           @RequestPart(name = "phoneNumber")String phonenumber,
//                                           @RequestPart(name = "db") String db) throws Exception{
//        ApiResponse apiResponse = service.updateMessage(newText, messageId, chatId, phonenumber, db);
//        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
//    }

//    @PostMapping(value = "deleteMessage",consumes = {"multipart/form-data"})
//    public ResponseEntity<?> deleteMessage(@RequestPart(name = "messageId") String messageId,
//                                           @RequestPart(name = "chatId") String chatId,
//                                           @RequestPart(name = "phoneNumber") String phoneNumber,
//                                           @RequestPart(name = "db") String db) throws Exception{
//        ApiResponse apiResponse = service.deleteMessage(messageId, chatId, phoneNumber, db);
//        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
//    }
//    @PostMapping(value = "addToGroups",consumes = {"multipart/form-data"})
//    public ResponseEntity<?> addToGroups(@RequestPart(name = "mainPhoneNumber") String mainPhoneNumber,
//                                         @RequestPart(name = "virtualPhoneNumber") String virtualPhoneNumber) throws Exception{
//        ApiResponse apiResponse = service.addCurrentAccountToGroups(mainPhoneNumber, virtualPhoneNumber);
//        return new ResponseEntity<>(apiResponse,HttpStatusCode.valueOf(apiResponse.getCode()));
//    }
}
