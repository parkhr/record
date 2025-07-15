package com.example.demo.storage;

import static com.example.demo.common.ErrorMessage.RECORD_ALREADY_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_IS_NOT_ON_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_AVAILABLE_FOR_LOAN;
import static com.example.demo.common.ErrorMessage.RECORD_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.record.entity.Records;
import com.example.demo.record.repository.RecordRepository;
import com.example.demo.storage.entity.StorageIn;
import com.example.demo.storage.entity.StorageOut;
import com.example.demo.storage.repository.StorageInRepository;
import com.example.demo.storage.repository.StorageOutRepository;
import com.example.demo.storage.request.CancelLoanRequest;
import com.example.demo.storage.request.LoanRequest;
import com.example.demo.storage.request.ReturnDelayRequest;
import com.example.demo.storage.request.ReturnRequest;
import java.util.List;
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

        List<StorageOut> storageOuts = storageOutRepository.findByRecordIdForUpdate(request.getRecordId());

        if (!storageOuts.isEmpty()) {
            throw new ApplicationException(RECORD_ALREADY_ON_LOAN);
        }

        return storageOutRepository.save(StorageOut.createStorageOut(request));
    }

    @Transactional
    public StorageIn returns(ReturnRequest request) {

        List<StorageOut> storageOuts = storageOutRepository.findByRecordId(request.getRecordId()).stream().filter(item -> !item.isDeleted()).toList();

        if (storageOuts.isEmpty()) {
            throw new ApplicationException(RECORD_IS_NOT_ON_LOAN);
        }

        StorageOut storageOut = storageOuts.get(0);

        storageOut.returns();
        storageOutRepository.save(storageOut);

        return storageInRepository.save(StorageIn.createStorageIn(request));
    }

    @Transactional
    public StorageOut delayReturn(ReturnDelayRequest request) {

        List<StorageOut> storageOuts = storageOutRepository.findByRecordId(request.getRecordId()).stream().filter(item -> !item.isDeleted()).toList();

        if (storageOuts.isEmpty()) {
            throw new ApplicationException(RECORD_IS_NOT_ON_LOAN);
        }

        StorageOut storageOut = storageOuts.get(0);

        storageOut.delayReturn();
        return storageOutRepository.save(storageOut);
    }

    @Transactional
    public StorageOut cancelLoan(CancelLoanRequest request) {

        StorageOut storageOut = storageOutRepository.findById(request.getStorageOutId())
            .orElseThrow(() -> new ApplicationException(RECORD_IS_NOT_ON_LOAN));

        if (storageOut.isDeleted()) {
            throw new ApplicationException(RECORD_IS_NOT_ON_LOAN);
        }

        storageOut.cancel();
        return storageOutRepository.save(storageOut);
    }
}
