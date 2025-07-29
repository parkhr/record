package com.example.demo.economy;

import com.example.demo.economy.domain.CardSmsParser;
import com.example.demo.economy.domain.CardSmsRecord;
import com.example.demo.economy.entity.Spend;
import com.example.demo.economy.repository.SearchSpendResponse;
import com.example.demo.economy.repository.SpendRepository;
import com.example.demo.economy.request.CreateSpendRequest;
//import com.example.demo.economy.request.UpdateSpendRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EconomyService {

    private final SpendRepository spendRepository;

    public Spend createSpend(CreateSpendRequest request) {
        CardSmsRecord cardSmsRecord = CardSmsParser.parse(request.getMessage());

        return spendRepository.save(Spend.createSpend(cardSmsRecord));
    }

    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, Pageable pageable) {
        return spendRepository.findSpends(request, pageable);
    }

//    public void updateSpend(UpdateSpendRequest request) {
//
//    }

//    public void deleteSpend(Long spendId) {
//
//    }
}
