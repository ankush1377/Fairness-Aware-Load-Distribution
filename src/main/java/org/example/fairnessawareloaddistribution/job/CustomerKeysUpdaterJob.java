package org.example.fairnessawareloaddistribution.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fairnessawareloaddistribution.entity.CustomerKeyProjection;
import org.example.fairnessawareloaddistribution.config.FairnessConfig;
import org.example.fairnessawareloaddistribution.entity.RecordStatus;
import org.example.fairnessawareloaddistribution.repository.RecordRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ankushs
 */
@Component
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
@Slf4j
public class CustomerKeysUpdaterJob {

    private final RecordRepository recordRepository;
    private final FairnessConfig fairnessConfig;

    @Scheduled(fixedRate = 10000)
    public void run() {
        Set<CustomerKeyProjection> results = recordRepository.findDistinctCustomerKeyByStatus(RecordStatus.QUEUED);
        Set<String> customerKeys = results.stream()
                .map(CustomerKeyProjection::getCustomerKey)
                .collect(Collectors.toSet());
        if (!customerKeys.isEmpty()) {
            fairnessConfig.setKeys(customerKeys);
            log.info("Fairness config: {}", fairnessConfig);
        }
    }
}
