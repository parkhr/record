package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.RECORD_ALREADY_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_IS_NOT_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_AVAILABLE_FOR_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Records;
import com.example.demo.entity.StorageIn;
import com.example.demo.entity.StorageOut;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.StorageInRepository;
import com.example.demo.repository.StorageOutRepository;
import com.example.demo.request.LoanRequest;
import com.example.demo.request.ReturnDelayRequest;
import com.example.demo.request.ReturnRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final RecordRepository recordRepository;
    private final StorageInRepository storageInRepository;
    private final StorageOutRepository storageOutRepository;

    @Transactional
    public StorageOut loan(LoanRequest request) {

        Records records = recordRepository.findById(request.getRecordId()).orElseThrow(() -> new ApplicationException(RECORD_NOT_FOUND));

        if (!records.isAvailableLoan()) {
            throw new ApplicationException(RECORD_NOT_AVAILABLE_FOR_LOAN);
        }

        Optional<StorageOut> storageOutOptional = storageOutRepository.findByRecordIdAndDeletedAtIsNullForUpdate(request.getRecordId());

        if (storageOutOptional.isPresent()) {
            throw new ApplicationException(RECORD_ALREADY_ON_LOAN);
        }

        return storageOutRepository.save(StorageOut.createStorageOut(request));
    }

    @Transactional
    public StorageIn returns(ReturnRequest request) {

        StorageOut storageOut = storageOutRepository.findByRecordIdAndDeletedAtIsNull(request.getRecordId())
            .orElseThrow(() -> new ApplicationException(RECORD_IS_NOT_ON_LOAN));

        storageOut.returns();
        storageOutRepository.save(storageOut);

        return storageInRepository.save(StorageIn.createStorageIn(request));
    }

    @Transactional
    public StorageOut delayReturn(ReturnDelayRequest request) {

        StorageOut storageOut = storageOutRepository.findByRecordIdAndDeletedAtIsNull(request.getRecordId())
            .orElseThrow(() -> new ApplicationException(RECORD_IS_NOT_ON_LOAN));

        storageOut.delayReturn();
        return storageOutRepository.save(storageOut);
    }
}
