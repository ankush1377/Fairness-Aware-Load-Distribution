package org.example.fairnessawareloaddistribution.dequeuer;

import lombok.extern.slf4j.Slf4j;
import org.example.fairnessawareloaddistribution.common.Observable;
import org.example.fairnessawareloaddistribution.common.Observer;
import org.example.fairnessawareloaddistribution.config.FairnessConfig;
import org.example.fairnessawareloaddistribution.entity.Record;
import org.example.fairnessawareloaddistribution.entity.RecordStatus;
import org.example.fairnessawareloaddistribution.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author ankushs
 */
@Component
@Slf4j
class RecordsDequeuer extends Observable<Record> {

    @Value("${node.name:unknown}")
    private String nodeName;

    private final RecordRepository recordRepository;
    private final FairnessConfig fairnessConfig;

    RecordsDequeuer(
            FairnessConfig fairnessConfig,
            RecordRepository recordRepository,
            List<Observer<Record>> recordProcessors
    ) {
        this.fairnessConfig = fairnessConfig;
        this.recordRepository = recordRepository;
        recordProcessors.forEach(this::addObserver);
    }

    @Scheduled(fixedRate = 3000)
    public void run() {
        Set<String> customerKeys = fairnessConfig.getKeysForNode(nodeName);
        if (!customerKeys.isEmpty()) {
            log.info("Customer keys for node {}: {}", nodeName, customerKeys);
            List<Record> records = dequeue(customerKeys, 2);
            notifyObservers(records);
        }
    }

    private List<Record> dequeue(Set<String> customerKeys, int size) {
        return recordRepository.findRecordsByStatusAndCustomerKeyIn(RecordStatus.QUEUED, customerKeys, PageRequest.of(0, size));
    }
}
