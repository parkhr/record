package com.example.demo.service;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Records;
import com.example.demo.repository.RecordRepository;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.SearchRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
import com.example.demo.request.UpdateRecordStatusRequest;
import com.example.demo.request.UpdateRecordVisibilityRequest;
import com.example.demo.response.SearchRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    @Transactional
    public Records createRecord(CreateRecordRequest request) {

        return recordRepository.save(Records.createTempRecord(request));
    }

    @Transactional
    public Records updateRecord(UpdateRecordRequest request) {

        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException("MSG"));

        record.update(request);

        return recordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(long recordId) {
        Records record = recordRepository.findById(recordId).orElseThrow(() -> new ApplicationException("MSG"));

        record.delete();
        recordRepository.save(record);
    }

    public Page<SearchRecordResponse> findPublicRecords(SearchRecordRequest request, Pageable pageable) {
        return recordRepository.findPublicRecords(request, pageable);
    }

    @Transactional
    public Records updateVisibility(UpdateRecordVisibilityRequest request) {
        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException("MSG"));

        record.updateVisibility(request);
        return recordRepository.save(record);
    }

    @Transactional
    public Records updateStatus(UpdateRecordStatusRequest request) {
        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException("MSG"));

        record.updateStatus(request);
        return recordRepository.save(record);
    }

}
