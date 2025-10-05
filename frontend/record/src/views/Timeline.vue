<template>
  <a-row>
    <a-col :span="10">
      <div class="day-timeline">
        <!-- 시간대 row -->
        <div v-for="(hour, index) in hours" :key="index" class="time-row">
          <!-- 시간 라벨 -->
          <div class="time-label">{{ hour }}</div>

          <!-- 시간 칸 -->
          <div class="time-cell" @click="handleTimeCellClick($event, hour)">
            <!-- 10분 눈금 -->
            <div
              v-for="i in 6"
              :key="i"
              class="minute-marker"
              :style="{ left: ((i - 1) / 6 * 100) + '%' }"
            ></div>

            <!-- 이벤트 표시 -->
            <div
              v-for="(event, eIndex) in getEventsAtHour(hour)"
              :key="eIndex"
              class="event-block"
              :style="getEventStyle(event)"
            >
              <div class="event-time">{{ event.startTime.split("T")[1] }} ~ {{ event.endTime.split("T")[1] }}</div>
              <div class="event-title">{{ event.title }} / {{ event.content }}</div>
            </div>

            <!-- 현재 시간 선 -->
            <div
              v-if="isCurrentHour(hour)"
              class="current-time-line"
              :style="{ left: getCurrentTimeLeft() + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </a-col>
    <a-col :span="14">
      <div>해야할 것 !</div>
        <ul>
          <li>설정 DB 설계</li>
          <li>설정 crud</li>
          <li>데일리리포트에 설정 반영</li>
          <li>데일리리포트 통계 (하루 피드백, 주간 피드백, 월간 피드백)</li>
          <li>타입, 몰입도, 컨디션 표시</li>
          <li>날짜 불일치 해결</li>
          <li>리포트 상세 설명 (오늘날짜... )</li>
        </ul>
    </a-col>
  </a-row>

  <ReportTaskCreateModal ref="reportTaskCreateModal" />
  <ReportTaskUpdateModal ref="reportTaskUpdateModal" />
</template>

<script lang="ts" setup>
import { fetchReport, fetchStatisticReport } from '@/api/reportApi';
import ReportTaskCreateModal from '@/components/ReportTaskCreateModal.vue';
import ReportTaskUpdateModal from '@/components/ReportTaskUpdateModal.vue';
import { ref, onMounted, onUnmounted } from 'vue';

const reportTaskCreateModal = ref();
const reportTaskUpdateModal = ref();

// ----------------------
// 상태 정의
// ----------------------
const hours = Array.from({ length: 16 }, (_, i) =>
  String(i + 8).padStart(2, "0") + ":00"
);

const report = ref(null);
const events = ref([]);

const currentTime = ref(new Date());

// ----------------------
// 유틸 함수
// ----------------------
const toMinutes = (time: string): number => {

  const [h, m] = time.split("T")[1].split(":").map(Number);
  return h * 60 + m;
};

// ----------------------
// 메서드
// ----------------------

const onCreate = (hourNum) => {
  reportTaskCreateModal.value.show(hourNum, report.value.id, () => {
    getReport();
  });
};

const onUpdate = (task) => {
  reportTaskUpdateModal.value.show(task, task.id, () => {
    getReport();
  });
};

const getEventsAtHour = (hour: string) => {
  const hourNum = parseInt(hour.split(":")[0], 10);
  const startMin = hourNum * 60;
  const endMin = (hourNum + 1) * 60;
  return events.value.filter(e => {
    const eStart = toMinutes(e.startTime);
    const eEnd = toMinutes(e.endTime);
    
    return eStart >= startMin && eEnd <= endMin;
  });
};

const getEventsAtMinute = (minute: number) => {
  return events.value.filter(e => {
    const eStart = toMinutes(e.startTime);
    const eEnd = toMinutes(e.endTime);
    return minute >= eStart && minute < eEnd;
  });
};

const getEventStyle = (event) => {
  const hourNum = parseInt(event.startTime.split("T")[1].split(":")[0], 10);
  const startOfHour = hourNum * 60;

  const startMin = toMinutes(event.startTime);
  const endMin = toMinutes(event.endTime);

  const leftPercent = ((startMin - startOfHour) / 60) * 100;
  const widthPercent = ((endMin - startMin) / 60) * 100;

  return {
    left: leftPercent + "%",
    width: widthPercent + "%",
    background: event.color,
    position: "absolute",
    height: "60%",
    top: "20%",
    borderRadius: "4px",
    color: "#444444",
    fontSize: "13px",
    padding: "2px 4px",
    overflow: "hidden",
    whiteSpace: "nowrap"
  };
};

const isCurrentHour = (hour: string) => {
  const hourNum = parseInt(hour.split(":")[0], 10);
  return currentTime.value.getHours() === hourNum;
};

const getCurrentTimeLeft = () => {
  const min = currentTime.value.getMinutes();
  const sec = currentTime.value.getSeconds();
  const totalMin = min + sec / 60;
  return (totalMin / 60) * 100;
};

const handleTimeCellClick = (e, hour) => {

  const cell = e.currentTarget as HTMLElement;
  const rect = cell.getBoundingClientRect();

  // 클릭한 위치 비율 (0 ~ 1)
  const clickRatio = (e.clientX - rect.left) / rect.width;

  // 시(hour)를 숫자로 변환
  const hourNum = parseInt(hour.split(":")[0], 10);

  // 클릭한 분 = 시*60 + (60 * 비율)
  const minutes = Math.floor(hourNum * 60 + clickRatio * 60);
  let reportTask = getEventsAtMinute(minutes);

  if(reportTask.length > 0) {
    let task = reportTask[0];
    onUpdate(task);
  }else {
    onCreate(hourNum);
  }
}

const getReport = async () => {

    const result = await fetchReport();

    report.value = result.data.report;
    events.value = result.data.reportTasks;

    const statisticResult = await fetchStatisticReport(report.value.id)
    console.log(statisticResult.data);
}
// ----------------------
// 타이머
// ----------------------
let timer: number;
onMounted(() => {
  timer = window.setInterval(() => {
    currentTime.value = new Date();
  }, 1000);
});

onMounted(() => {
  getReport();
});

onUnmounted(() => {
  clearInterval(timer);
});
</script>

<style scoped>
.day-timeline {
  display: flex;
  flex-direction: column;
  margin-left: 80px;
}

.time-row {
  display: flex;
  align-items: center;
  height: 76px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.time-label {
  position: absolute;
  left: -80px;
  width: 70px;
  text-align: right;
  font-size: 13px;
  color: #888;
}

.time-cell {
  flex: 1;
  position: relative;
  background: #fafafa;
  border-left: 1px solid #eee;
  height: 100%;
}

.minute-marker {
  position: absolute;
  top: 0;
  width: 1px;
  height: 100%;
  background: #e0e0e0;
  z-index: 1;
}

.event-block {
  position: absolute;
  z-index: 2;
}

.current-time-line {
  position: absolute;
  top: 0;
  width: 2px;
  height: 100%;
  background: red;
  z-index: 3;
}
</style>
