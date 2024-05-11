package org.example.fairnessawareloaddistribution.processor;

import lombok.extern.slf4j.Slf4j;
import org.example.fairnessawareloaddistribution.common.Observer;
import org.example.fairnessawareloaddistribution.entity.Record;
import org.example.fairnessawareloaddistribution.entity.RecordStatus;
import org.example.fairnessawareloaddistribution.repository.RecordRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ankushs
 */
@Component
@Slf4j
public class RecordsProcessor extends Observer<Record> {

    private final RecordRepository recordRepository;

    public RecordsProcessor(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public void update(List<Record> records) {
        records.forEach(record -> {
            log.info("Processing record: {}", record);
            record.setStatus(RecordStatus.SUCCEEDED);
        });
        recordRepository.saveAll(records);
    }
}
