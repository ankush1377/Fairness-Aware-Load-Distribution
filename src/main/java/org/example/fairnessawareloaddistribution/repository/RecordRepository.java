package org.example.fairnessawareloaddistribution.repository;

import org.example.fairnessawareloaddistribution.CustomerKeyProjection;
import org.example.fairnessawareloaddistribution.entity.Record;
import org.example.fairnessawareloaddistribution.entity.RecordStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * @author ankushs
 */
public interface RecordRepository extends MongoRepository<Record, String> {
    List<Record> findRecordsByStatusAndCustomerKeyIn(RecordStatus status, Set<String> customerKeys, Pageable pageable);

    @Query(value = "{'status': ?0}", fields = "{'customerKey': 1}")
    Set<CustomerKeyProjection> findDistinctCustomerKeyByStatus(RecordStatus status);
}
