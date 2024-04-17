package uz.mh.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;
import uz.mh.messenger.config.TdLibConfig;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.dto.TgmData;
import uz.mh.messenger.enums.StatementStatus;
import uz.mh.messenger.mapper.StatementMapper;
import uz.mh.messenger.model.Statement;
import uz.mh.messenger.repository.StatementRepository;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StatementService {
    private final StatementMapper statementMapper;
    private final StatementRepository statementRepository;
    private final TdlightService tdlightService;
    private final TdLibConfig config;
    private final IntegrateWithEgs egs;
    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;
    ExecutorService executor = Executors.newFixedThreadPool(5);
    public StatementService(StatementMapper statementMapper, StatementRepository statementRepository, TdlightService tdlightService, TdLibConfig config, IntegrateWithEgs egs) {
        this.statementMapper = statementMapper;
        this.statementRepository = statementRepository;
        this.tdlightService = tdlightService;
        this.config = config;
        this.egs = egs;
    }

    public void saveStatement(StatementDto statementDto) {
        Optional<Statement> statementOptional = statementRepository.findByStatementId(statementDto.getStatementId());
        if (statementOptional.isEmpty()) {
            Statement statement = statementMapper.mapDtoToStatement(statementDto);
            statementRepository.save(statement);
        }else {
            statementRepository.editStatement(statementDto.getStatementId());
            statementDto.setStatus(StatementStatus.CLOSED);
        }
        sendGroups(statementDto);
    }

    private void sendGroups(StatementDto statementDto) {
        if (statementDto.getStatus().equals(StatementStatus.ACTUAL)){
            try {
                int count = tdlightService.sendMessageToGroup("+998948183201", statementDto.getStatementId() + statementDto.getText(), "egs");
                TgmData data = new TgmData(statementDto.getStatementId().substring(1),count,count);
                System.out.println(data);
                List<TgmData> dataList = List.of(data);
                egs.sendToEgs(dataList,"egs");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Scheduled(cron = "0 */10 * * * *")
    public void sendPeriodic(){
        try {
            Optional<List<Statement>> actualStatements = statementRepository.getActualStatements();
            if (actualStatements.isPresent()) {
                System.out.println("ishladi");
                List<Statement> statements = actualStatements.get();
                System.out.println(statements.size());
                for (Statement statement : statements) {
                    executor.submit(new TdlightService("+998948183201",statement.getStatementId() + statement.getText(),"egs",config));
                }
//                executor.shutdown();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
