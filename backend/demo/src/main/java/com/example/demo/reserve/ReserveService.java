package com.example.demo.reserve;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.reserve.entity.Reserve;
import com.example.demo.reserve.repository.ReserveRepository;
import com.example.demo.reserve.request.CancelReserveRequest;
import com.example.demo.reserve.request.CreateReserveRequest;
import com.example.demo.storage.entity.StorageOut;
import com.example.demo.storage.repository.StorageOutRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final StorageOutRepository storageOutRepository;
    private final ReserveRepository reserveRepository;

    @Transactional
    public Reserve reserve(CreateReserveRequest request) {

        List<StorageOut> storageOuts = storageOutRepository.findByRecordIdForUpdate(request.getRecordId()).stream().filter(item -> !item.isDeleted())
            .toList();

        if (storageOuts.isEmpty()) {
            throw new ApplicationException("현재 대출할 수 있는 기록물입니다.");
        }

        List<Reserve> reserves = reserveRepository.findByRecordIdAndUserId(request.getRecordId(), request.getUserId()).stream()
            .filter(item -> !item.isDeleted()).toList();

        if (!reserves.isEmpty()) {
            throw new ApplicationException("이미 예약한 기록물입니다.");
        }

        return reserveRepository.save(Reserve.createReserve(request));
    }

    @Transactional
    public Reserve cancelReserve(CancelReserveRequest request) {

        Reserve reserve = reserveRepository.findById(request.getReserveId()).orElseThrow(() -> new ApplicationException("예약 데이터를 찾을 수 없습니다."));

        if (reserve.isDeleted()) {
            throw new ApplicationException("예약 데이터를 찾을 수 없습니다.");
        }

        reserve.cancel();

        return reserveRepository.save(reserve);
    }
}
