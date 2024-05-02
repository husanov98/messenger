package uz.mh.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;
import uz.mh.messenger.config.TdLibConfig;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.enums.StatementStatus;
import uz.mh.messenger.mapper.StatementMapper;
import uz.mh.messenger.model.ApiResponse;
import uz.mh.messenger.model.Manager;
import uz.mh.messenger.model.Statement;
import uz.mh.messenger.repository.ManagerRepository;
import uz.mh.messenger.repository.StatementRepository;
import uz.mh.messenger.response.MessageResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StatementService {
    private final StatementMapper statementMapper;
    private final StatementRepository statementRepository;
    private final TdLibConfig config;
    private final IntegrateWithEgs egs;
    private final ManagerRepository managerRepository;
    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;
    private final List<String> statuses = new ArrayList<>();

    ExecutorService executor = Executors.newFixedThreadPool(5);
    public StatementService(StatementMapper statementMapper, StatementRepository statementRepository, TdLibConfig config, IntegrateWithEgs egs, ManagerRepository managerRepository) {
        this.statementMapper = statementMapper;
        this.statementRepository = statementRepository;
        this.config = config;
        this.egs = egs;
        this.managerRepository = managerRepository;
    }

    public void saveStatement(StatementDto statementDto) {
        Optional<Statement> statementOptional = statementRepository.findByStatementId(statementDto.getStatementId());
            if (statementOptional.isEmpty()) {
                Statement statement = statementMapper.mapDtoToStatement(statementDto);
                statementRepository.save(statement);
            }else if (statementDto.getStatus().equals(StatementStatus.CLOSED)){
                statementRepository.editStatementStatusToClosed(statementDto.getStatementId());
            }
    }

//    private void sendGroups(StatementDto statementDto) {
//        Optional<Manager> optionalManager = managerRepository.findByPhoneNumber(statementDto.getPhoneNumber());
//        if (statementDto.getStatus().equals(StatementStatus.ACTUAL) && optionalManager.isPresent()){
//            try {
//                tdlightService.sendMessageToGroup(statementDto);
//                statementRepository.editStatementSentCount(statementDto.getGroupCount(), statementDto.getSentCount(),statementDto.getStatementId());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else {
//            System.out.println("Bu bratan hali ro'yxatdan o'tmagan yoki zapros yopilgan");
//        }
//    }
    @Scheduled(cron = "0 */10 4-20 * * *")
    public void sendPeriodic(){
        try {
            Optional<List<Statement>> actualStatements = statementRepository.getActualStatements();
            if (actualStatements.isPresent()) {
                List<Statement> statements = actualStatements.get();
                System.out.println(statements.size());
                for (Statement statement : statements) {
                    StatementDto statementDto = statementMapper.mapToStatementDto(statement);
                    Optional<Manager> optionalManager = managerRepository.findByPhoneNumber(statementDto.getPhoneNumber());
                    if (optionalManager.isPresent() && statementDto.getSentCount() < 500) {
                        executor.submit(new TdlightService(config,managerRepository,statementDto, egs, statementRepository));
                    }else {
                        System.out.println("Bu bratan hali ro'yxatdan o'tmagan");
                    }
                }
//                executor.shutdown();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public ApiResponse updateStatementStatus(String status, String statementId){
        ApiResponse apiResponse = new ApiResponse();
        Optional<Statement> statementOptional = statementRepository.findByStatementId("#" + statementId);
        if (statementOptional.isPresent()){
            if (status.equals("В поиске перевозчика ") || status.equals("Актуальный")){
                statementRepository.editStatementStatusToActual("#" + statementId);
            }else{
                statementRepository.editStatementStatusToClosed("#" + statementId);
            }
            apiResponse.setCode(200);
            apiResponse.setSuccess(true);
            apiResponse.setContent(new MessageResponse("Statement status changed successfully"));
            apiResponse.setError("");
        }else {
            apiResponse.setCode(404);
            apiResponse.setSuccess(false);
            apiResponse.setContent(new MessageResponse(""));
            apiResponse.setError("Statement not found");
        }
        return apiResponse;
    }
}
