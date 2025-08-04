<template>
  <a-card title="이번주 수입" bordered hoverable>
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
      label: '수입',
      data: [], // 초기값은 비워둠
      borderColor: '#4caf50',
      backgroundColor: 'rgba(76, 175, 80, 0.2)',
      fill: true,
      tension: 0.3,
    },
  ],
});

const incomes = ref([]);

const fetchThisWeekIncome = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/active', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    incomes.value = response.data.amounts;
    chartData.value.datasets[0].data = incomes.value;
  } catch (error) {
    message.error('이번주수입을 불러올 수 없습니다.');
  }
}

onMounted(() => {
  fetchThisWeekIncome();
})


</script>