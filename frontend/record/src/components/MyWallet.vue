<template>
  <a-card title="내 지갑" bordered hoverable>
    <a-row justify="space-between">
      <a-col>
        <div style="font-size: 18px; color: #888;">총 잔액</div>
        <div style="font-size: 32px; font-weight: bold;">₩ {{ formatNumber(walletData.amount) }}</div>

        <div v-if="walletData.amount < 0" style="margin-top: 12px; padding: 10px; background: #fff8f0; border-radius: 8px; color: #d46b08; font-size: 14px;">
          <span style="font-weight: bold;">💡 목표까지 남은 시간:</span><br>
          <span style="font-size: 16px; font-weight: bold; color: #fa8c16;">
            {{ breakData.hour }}시간 {{ breakData.minutes }}분
          </span>
          활동하면 손익분기점에 도달해요! 🔥
        </div>

        <div v-else 
             style="margin-top: 12px; padding: 10px; background: #f6ffed; border-radius: 8px; color: #389e0d; font-size: 14px;">
          🎉 현재 흑자 상태입니다! 여유롭게 관리하세요 😊
        </div>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
import api from '@/api/axios';
import { message } from 'ant-design-vue';
import { onMounted, ref } from 'vue';

const walletData = ref({
  amount: 0
});

const breakData = ref({
  hour: 0,
  minutes: 0
});

const formatNumber = (num) => num?.toLocaleString() ?? '0';

const fetchWallet = async () => {
  try {
    const response = await api.get('/api/economy/wallet',{
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });

    walletData.value = response.data;
  } catch (error) {
    message.error('지갑 정보를 불러오는 데 실패했습니다.');
  }
}

const fetchBreakEvenTime = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/break-even-time', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    breakData.value.hour = response.data.hour;
    breakData.value.minutes = response.data.minutes;
  } catch (error) {
    message.error('손익 분기점을 불러오는 데 실패했습니다.');
  }
}

onMounted(() => {
  fetchWallet();
  fetchBreakEvenTime();
});
</script>