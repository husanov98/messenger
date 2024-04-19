package uz.mh.messenger.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.model.Statement;


@Component
public class StatementMapper {
    private static final ModelMapper modelMapper = new ModelMapper();
    public Statement mapDtoToStatement(StatementDto statementDto){return modelMapper.map(statementDto,Statement.class);}
    public StatementDto mapToStatementDto(Statement statement){return modelMapper.map(statement, StatementDto.class);}
}
