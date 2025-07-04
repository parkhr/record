package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.COLLECTION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.FOLDER_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.INVALID_RECORD_LOCATION_TYPE;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.SERIES_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.RecordLocation;
import com.example.demo.entity.Records;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.RecordLocationRepository;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.SeriesRepository;
import com.example.demo.request.CreateRecordRequest;
import com.example.demo.request.DispatchRecordRequest;
import com.example.demo.request.SearchRecordRequest;
import com.example.demo.request.UpdateRecordRequest;
import com.example.demo.request.UpdateRecordStatusRequest;
import com.example.demo.request.UpdateRecordVisibilityRequest;
import com.example.demo.response.SearchRecordResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final CollectionRepository collectionRepository;
    private final SeriesRepository seriesRepository;
    private final FolderRepository folderRepository;
    private final RecordLocationRepository recordLocationRepository;

    @Transactional
    public Records createRecord(CreateRecordRequest request) {

        return recordRepository.save(Records.createTempRecord(request));
    }

    @Transactional
    public Records updateRecord(UpdateRecordRequest request) {

        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        record.update(request);

        return recordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(long recordId) {
        Records record = recordRepository.findById(recordId).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        record.delete();
        recordRepository.save(record);
    }

    public Page<SearchRecordResponse> findPublicRecords(SearchRecordRequest request, Pageable pageable) {
        return recordRepository.findPublicRecords(request, pageable);
    }

    @Transactional
    public Records updateVisibility(UpdateRecordVisibilityRequest request) {
        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        record.updateVisibility(request);
        return recordRepository.save(record);
    }

    @Transactional
    public Records updateStatus(UpdateRecordStatusRequest request) {
        Records record = recordRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        record.updateStatus(request);
        return recordRepository.save(record);
    }

    @Transactional
    public RecordLocation dispatchRecord(DispatchRecordRequest request) {

        recordRepository.findById(request.getRecordId()).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        switch (request.getLocationType()) {
            case COLLECTION ->
                collectionRepository.findById(request.getLocationId()).orElseThrow(() -> new ApplicationException(COLLECTION_NOT_FOUND));
            case SERIES -> seriesRepository.findById(request.getLocationId()).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));
            case FOLDER -> folderRepository.findById(request.getLocationId()).orElseThrow(() -> new ApplicationException(FOLDER_NOT_FOUND));
            default -> throw new ApplicationException(INVALID_RECORD_LOCATION_TYPE);
        }

        RecordLocation recordLocation = RecordLocation.builder()
            .recordId(request.getRecordId())
            .locationId(request.getLocationId())
            .locationType(request.getLocationType())
            .createdAt(LocalDateTime.now())
            .build();

        return recordLocationRepository.save(recordLocation);
    }
}
