package br.walleyz.aws_course_p2.repository;

import br.walleyz.aws_course_p2.model.ProductEventKey;
import br.walleyz.aws_course_p2.model.ProductEventLog;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface ProductEventLogRepository extends CrudRepository<ProductEventLog, ProductEventKey> {

    List<ProductEventLog> findAllByPk(String pk);
    List<ProductEventLog> findAllByPkAndSkStartsWith(String pk, String eventType);
}
