<template>
  <a-card title="이번주 지출" bordered hoverable>
    <LineChart :chartData="chartData" />
  </a-card>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { LineChart } from 'vue-chart-3';
import { Chart, registerables } from "chart.js";
import api from '@/api/axios';
import { message } from 'ant-design-vue';

Chart.register(...registerables);

const chartData = ref({
  labels: ['월', '화', '수', '목', '금', '토', '일'],
  datasets: [
    {
      label: '지출',
      data: [],
      borderColor: '#f44336', // 빨간색 선
      backgroundColor: 'rgba(244, 67, 54, 0.2)', // 빨간색 배경 (투명도 조절)
      fill: true,
      tension: 0.3,
    },
  ],
});

const fetchThisWeekSpend = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/spend', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    chartData.value.datasets[0].data = response.data.amounts;
  } catch (error) {
    message.error('이번주지출을 불러올 수 없습니다.');
  }
}

onMounted(() => {
  fetchThisWeekSpend();
})


</script>