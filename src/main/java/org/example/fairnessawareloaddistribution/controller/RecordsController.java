package org.example.fairnessawareloaddistribution.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fairnessawareloaddistribution.entity.Record;
import org.example.fairnessawareloaddistribution.entity.RecordStatus;
import org.example.fairnessawareloaddistribution.repository.RecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author ankushs
 */
@RestController
@RequestMapping("/api/v1/records")
@Slf4j
@AllArgsConstructor
public class RecordsController {

    private final RecordRepository recordRepository;

    // Create a Record
    @PostMapping(value = "", produces = "application/json")
    public Record addRecord(@RequestBody Record record) {
        record.setStatus(RecordStatus.QUEUED);
        return recordRepository.save(record);
    }

    // Read all Records
    @GetMapping(value = "/", produces = "application/json")
    public List<Record> getAllRecords() {
        return recordRepository.findAll();
    }

    // Read a single Record
    @GetMapping(value = "/{id}", produces = "application/json")
    public Optional<Record> getRecord(@PathVariable String id) {
        return recordRepository.findById(id);
    }

    // Update a Record
    @PutMapping(value = "/{id}", produces = "application/json")
    public Record updateRecord(@PathVariable String id, @RequestBody Record updatedRecord) {
        return recordRepository.findById(id)
                .map(record -> {
                    record.setCustomerKey(updatedRecord.getCustomerKey());
                    record.setStatus(updatedRecord.getStatus());
                    return recordRepository.save(record);
                })
                .orElseGet(() -> {
                    updatedRecord.setId(id);
                    return recordRepository.save(updatedRecord);
                });
    }

    // Delete a Record
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public void deleteRecord(@PathVariable String id) {
        recordRepository.deleteById(id);
    }
}
