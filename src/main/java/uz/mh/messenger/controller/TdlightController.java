package uz.mh.messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.*;
import uz.mh.messenger.exceptions.SchedulerNotStartException;
import uz.mh.messenger.model.ApiResponse;
import uz.mh.messenger.service.GetNotificationService;
import uz.mh.messenger.service.TdlightService;


@RestController
//@CrossOrigin(origins = "*")
public class TdlightController {

    private boolean isStart = false;

    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;
    private final TdlightService service;
    private final GetNotificationService notificationService;

    public TdlightController(TdlightService service, GetNotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }


    @PostMapping(value = "/sendPhoneNumber",consumes = {"multipart/form-data"})
    public ResponseEntity<?> send(@RequestPart(name = "phoneNumber") String phoneNumber) throws Exception {
        ApiResponse apiResponse = service.registerwithPhoneNumber(phoneNumber);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
    }

    @PostMapping(value = "/sendCode",consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendCode(@RequestPart(name = "phoneNumber") String phoneNumber,
                                      @RequestPart(name = "code") String code) throws Exception {
        ApiResponse apiResponse = service.enterAuthenticationCode(phoneNumber, code);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getCode()));
    }
//    @GetMapping(value = "/get")
//    public void get()throws Exception{
//        notificationService.getNotifications();
//    }
//
//    @PostMapping(value = "getChats",consumes = {"multipart/form-data"})
//    public ResponseEntity<?>getChats(@RequestPart(name = "phoneNumber") String phoneNumber) throws Exception{
//        ApiResponse apiResponse = service.getChats(phoneNumber);
//        return new ResponseEntity<>(apiResponse,HttpStatusCode.valueOf(apiResponse.getCode()));
//    }
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

    @PostMapping("/start")
    public ResponseEntity<String> start(){
        isStart = true;
        return ResponseEntity.ok("Getting new messages started");
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void getNotification(){
        try {
            if (isStart){
                service.getChats("+998933899112");
                notificationService.getNotifications();
            }else {
                throw new SchedulerNotStartException("Scheduler is not working exception");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
