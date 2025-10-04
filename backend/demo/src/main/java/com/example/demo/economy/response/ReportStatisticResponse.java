package com.example.demo.economy.response;

import com.example.demo.economy.entity.ReportTask;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportStatisticResponse {

    private Map<String, List<ReportTask>> time;
    private Map<String, List<ReportTask>> hard;
    private Map<String, List<ReportTask>> condition;
}
