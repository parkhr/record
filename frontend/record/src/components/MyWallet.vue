<template>
  <a-card title="내 지갑" bordered hoverable>
    <a-row justify="space-between">
      <a-col>
        <div style="font-size: 18px; color: #888;">총 잔액</div>
        <div style="font-size: 32px; font-weight: bold;">₩ {{ formatNumber(walletData.amount) }}</div>
        <a-row :gutter="[50, 10]">
          <a-col :span="12">
            <div style="font-size: 14px; color: #888;">이번주 총 수입</div>
            <div style="font-size: 23px; color: #4caf50; font-weight: bold;">₩ {{ formatNumber(totalThisWeekIncome) }}</div>
          </a-col>
          <a-col :span="12">
            <div style="font-size: 14px; color: #888;">이번주 총 지출</div>
            <div style="font-size: 23px; color: #f44336; font-weight: bold;">₩ {{ formatNumber(totalThisWeekSpend) }}</div>
          </a-col>
          <a-col :span="12">
            <div style="font-size: 14px; color: #888;">이번달 총 수입</div>
            <div style="font-size: 23px; color: #4caf50; font-weight: bold;">₩ {{ formatNumber(totalThisMonthIncome) }}</div>
          </a-col>
          <a-col :span="12">
            <div style="font-size: 14px; color: #888;">이번달 총 지출</div>
            <div style="font-size: 23px; color: #f44336; font-weight: bold;">₩ {{ formatNumber(totalThisMonthSpend) }}</div>
          </a-col>
        </a-row>

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

const totalThisWeekIncome = ref(0);
const totalThisWeekSpend = ref(0);
const totalThisMonthIncome = ref(0);
const totalThisMonthSpend = ref(0);

const formatNumber = (num) => num?.toLocaleString() ?? '0';

const fetchWallet = async () => {
  try {
    const response = await api.get('/api/economy/wallet',{
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });
    if(response.status !== 200) throw new Error();

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
    if(response.status !== 200) throw new Error();

    breakData.value.hour = response.data.hour;
    breakData.value.minutes = response.data.minutes;
  } catch (error) {
    message.error('손익 분기점을 불러오는 데 실패했습니다.');
  }
}

const fetchThisWeekSpend = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/spend', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    if(response.status !== 200) throw new Error();

    const total = response.data.amounts.reduce((sum, value) => sum + value, 0);
    totalThisWeekSpend.value = total;
  } catch (error) {
    message.error('이번주지출을 불러올 수 없습니다.');
  }
}

const fetchThisWeekIncome = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/active', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    if(response.status !== 200) throw new Error();

    const total = response.data.amounts.reduce((sum, value) => sum + value, 0);
    totalThisWeekIncome.value = total;
  } catch (error) {
    message.error('이번주수입을 불러올 수 없습니다.');
  }
}

const fetchThisMonthIncome = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/active/month', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    if(response.status !== 200) throw new Error();

    const total = response.data.amounts.reduce((sum, value) => sum + value, 0);
    totalThisMonthIncome.value = total;
    
  } catch (error) {
    message.error('이번달수입을 불러올 수 없습니다.');
  }
}

const fetchThisMonthSpend = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/spend/month', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    if(response.status !== 200) throw new Error();

    const total = response.data.amounts.reduce((sum, value) => sum + value, 0);
    totalThisMonthSpend.value = total;
    
  } catch (error) {
    message.error('이번달지출을 불러올 수 없습니다.');
  }
}


onMounted(() => {
  fetchWallet();
  fetchBreakEvenTime();
  fetchThisWeekSpend();
  fetchThisWeekIncome();
  fetchThisMonthIncome();
  fetchThisMonthSpend();
});
</script>