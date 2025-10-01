<template>
  <a-row>
    <a-col :span="10">
      <div class="day-timeline">
        <!-- 시간대 row -->
        <div v-for="(hour, index) in hours" :key="index" class="time-row">
          <!-- 시간 라벨 -->
          <div class="time-label">{{ hour }}</div>

          <!-- 시간 칸 -->
          <div class="time-cell">
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
              <div class="event-time">{{ event.start }} ~ {{ event.end }}</div>
              <div class="event-title">{{ event.title }} / {{ event.desc }}</div>
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
            <li>데일리리포트 생성(스케줄러로 새벽 4시쯤 데일리리포트 생성)</li>
            <li>반복되는 고정 데이터 저장 및 백엔드 연동 ex) 아침루틴</li>
            <li>데일리리포트 데이터 등록(좀 더 쉽게 등록할 수 있는 방법 고민 ...)</li>
            <li>데일리리포트 데이터 수정</li>
            <li>데일리리포트 데이터 삭제</li>
            <li>데일리리포트 통계 (하루 피드백, 주간 피드백, 월간 피드백)</li>
        </ul>
        
    </a-col>
  </a-row>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue';

// ----------------------
// 타입 정의
// ----------------------
interface EventItem {
  title: string;
  start: string; // "HH:MM"
  end: string;   // "HH:MM"
  desc: string;
  color: string;
}

// ----------------------
// 상태 정의
// ----------------------
const hours = Array.from({ length: 16 }, (_, i) =>
  String(i + 8).padStart(2, "0") + ":00"
);

const events = ref<EventItem[]>([
  { title: "아침 루틴 & 출근", start: "08:15", desc: "물 500ml 마시기, 스트레칭, 샤워, 출근, 할일 정리", end: "09:00", color: "#1677ff" },
  { title: "회의", start: "09:00", desc: "팀 미팅", end: "10:00", color: "#fa8c16" },
  { title: "점심 식사", start: "12:00", desc: "식사, 가벼운 산책 / 자리에서 스트레칭, 영어단어 외우기", end: "13:00", color: "#13c2c2" },
  { title: "프로젝트 작업", start: "14:00", desc: "코딩 및 문서 작성", end: "17:00", color: "#eb2f96" },
  { title: "운동", start: "20:00", desc: "헬스장 가기", end: "21:00", color: "#f5222d" },
  { title: "휴식", start: "21:00", desc: "쉬기", end: "21:30", color: "#fa541c" },
  { title: "작업", start: "21:30", desc: "몰입 창의 작업 (아이디어 구상, 사이드 프로젝트 설계)", end: "22:00", color: "#52c41a" },
  { title: "작업", start: "22:00", desc: "몰입 창의 작업 (아이디어 구상, 사이드 프로젝트 설계)", end: "22:40", color: "#52c41a" },
  { title: "작업", start: "23:00", desc: "마무리 루틴 (독서)", end: "24:00", color: "#1677ff" },
]);

const currentTime = ref(new Date());

// ----------------------
// 유틸 함수
// ----------------------
const toMinutes = (time: string): number => {
  const [h, m] = time.split(":").map(Number);
  return h * 60 + m;
};

// ----------------------
// 메서드
// ----------------------
const getEventsAtHour = (hour: string) => {
  const hourNum = parseInt(hour.split(":")[0], 10);
  const startMin = hourNum * 60;
  const endMin = (hourNum + 1) * 60;
  return events.value.filter(e => {
    const eStart = toMinutes(e.start);
    const eEnd = toMinutes(e.end);
    return eStart >= startMin && eEnd <= endMin;
  });
};

const getEventStyle = (event: EventItem) => {
  const hourNum = parseInt(event.start.split(":")[0], 10);
  const startOfHour = hourNum * 60;

  const startMin = toMinutes(event.start);
  const endMin = toMinutes(event.end);

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
    color: "#fff",
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

// ----------------------
// 타이머
// ----------------------
let timer: number;
onMounted(() => {
  timer = window.setInterval(() => {
    currentTime.value = new Date();
  }, 1000);
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
