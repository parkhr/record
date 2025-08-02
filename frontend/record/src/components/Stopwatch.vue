<template>
  <div style="border: 1px solid #d9d9d9; border-radius: 2%; padding: 16px; margin-bottom: 16px;">
    <div style="margin-bottom: 16px;">
      <a-button type="primary" @click="startTimer" :disabled="isRunning">시작</a-button>
      <a-button @click="pauseTimer" :disabled="!isRunning">일시정지</a-button>
      <a-button danger @click="resetTimer">초기화</a-button>
      <a-button type="primary" @click="onCreate">등록</a-button>
    </div>

    <div style="font-size: 32px;">
      {{ formatTime(elapsedSeconds) }}
    </div>
  </div>
  
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import api from '@/api/axios';
// const active = ... // active 객체가 어디서 오는지 명시적으로 추가 필요

const STORAGE_KEY = 'stopwatch-seconds';

const elapsedSeconds = ref(0);
const isRunning = ref(false);
let timer = null;

onMounted(() => {
  const stored = localStorage.getItem(STORAGE_KEY);
  if (stored) {
    elapsedSeconds.value = Number(stored);
  }
});

watch(elapsedSeconds, (val) => {
  localStorage.setItem(STORAGE_KEY, val.toString());
});

const formatTime = (totalSeconds) => {
  const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
  const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
  const seconds = String(totalSeconds % 60).padStart(2, '0');
  return `${hours}:${minutes}:${seconds}`;
};

const startTimer = () => {
  if (isRunning.value) return;
  isRunning.value = true;

  timer = setInterval(() => {
    elapsedSeconds.value++;
  }, 1000);
};

const pauseTimer = () => {
  clearInterval(timer);
  isRunning.value = false;
};

const resetTimer = () => {
  clearInterval(timer);
  isRunning.value = false;
  elapsedSeconds.value = 0;
  localStorage.removeItem(STORAGE_KEY);
};

const emit = defineEmits(['refreshByChild']);
const onCreate = async () => {
  try {
    const minutes = Math.floor(elapsedSeconds.value / 60); // 초 → 분 (내림)

    // 0분 이상일때만 등록
    if (minutes <= 0) {
      message.error('활동시간이 0분입니다. 시간을 측정한 후 등록해주세요.');
      return;
    } else {
      const response = await api.post(
        '/api/economy/active',
        {
          minutes: minutes, // 시간도 같이 보내면 좋음
        },
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`,
          }
        }
      );

      // 등록 후 타이머 초기화
      resetTimer();

      emit('refreshByChild');

      message.success('활동내역이 등록되었습니다.');
    }
  } catch (error) {
    message.error('활동내역 등록 실패');
  }
};
</script>