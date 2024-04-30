package uz.mh.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mh.messenger.model.Statement;


import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement,Long> {
    @Query(nativeQuery = true,value = "update statements set updated_at = now() at time zone 'Asia/Tashkent' where statement_id =:statementId returning *")
    Optional<Statement> updateStatementUpdatedAt(String statementId);
    @Query(nativeQuery = true,value = "select message_id from statements as s order by s.created_at desc limit 1")
    Long finByCratedAt();
    @Query(nativeQuery = true,value = "select * from statements where statements.statement_id =:statementId")
    Optional<Statement> findByStatementId(String statementId);
    @Query(nativeQuery = true,value = "update statements set status = 'CLOSED' where statement_id =:statementId returning *")
    Optional<Statement> editStatementStatusToClosed(String statementId);
    @Query(nativeQuery = true,value = "select * from statements where now() at time zone 'Asia/Tashkent' - statements.updated_at >= interval '120 minutes' and statements.status = 'ACTUAL'")
    Optional<List<Statement>> getActualStatements();
    @Query(nativeQuery = true,value = "update statements set group_count =:groupCount, sent_count =:sentCount where statement_id =:statementId returning *")
    Optional<Statement> editStatementSentCount(Integer groupCount,Integer sentCount,String statementId);
    @Query(nativeQuery = true,value = "update statements set status = 'ACTUAL' where statement_id =:statementId returning *")
    Optional<Statement> editStatementStatusToActual(String statementId);
}
