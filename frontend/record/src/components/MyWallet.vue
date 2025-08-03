<template>
  <a-card title="내 지갑" bordered hoverable>
    <a-row justify="space-between">
      <a-col>
        <div style="font-size: 18px; color: #888;">총 잔액</div>
        <div style="font-size: 32px; font-weight: bold;">₩ {{ walletData.amount }}</div>
      </a-col>
    </a-row>
  </a-card>
</template>

<script lang="ts" setup>
import api from '@/api/axios';
import { message } from 'ant-design-vue';
import { onMounted, ref } from 'vue';

const walletData = ref({
  amount: 0,
  lastSaved: 0,
  lastSpend: 0
});

onMounted(() => {
  fetchWallet();
});

const fetchWallet = async () => {
  try {
    const response = await api.get('/api/economy/wallet',{
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });

    walletData.value = response.data;
    console.log(walletData.value);
  } catch (error) {
    message.error('지갑 정보를 불러오는 데 실패했습니다.');
  }
}
</script>