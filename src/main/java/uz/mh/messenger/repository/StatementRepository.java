package uz.mh.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mh.messenger.model.Statement;


import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement,Long> {
    @Query(nativeQuery = true,value = "select * from statements where statements.statement_id =:statementId")
    Optional<Statement> findByStatementId(String statementId);
    @Query(nativeQuery = true,value = "update statements set status = 'CLOSED' where statement_id =:statementId returning *")
    Optional<Statement> editStatement(String statementId);
    @Query(nativeQuery = true,value = "select * from statements where now() at time zone 'Asia/Tashkent' - statements.updated_at >= interval '30 minutes' and statements.status = 'ACTUAL'")
    Optional<List<Statement>> getActualStatements();
    @Query(nativeQuery = true,value = "update statements set group_count =:groupCount, sent_count =:sentCount where statement_id =:statementId returning *")
    Optional<Statement> editStatementSentCount(Integer groupCount,Integer sentCount,String statementId);
}
