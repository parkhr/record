<template>
  <a-card title="이번달 수입" bordered hoverable>
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
  labels: [
    '1', '2', '3', '4', '5', 
    '6', '7', '8', '9', '10', 
    '11', '12', '13', '14', '15', 
    '16', '17', '18', '19', '20',
    '21', '22', '23', '24', '25',
    '26', '27', '28', '29', '30'
  ],
  datasets: [
    {
      label: '수입',
      data: [],
      borderColor: '#4caf50',
      backgroundColor: 'rgba(76, 175, 80, 0.2)',
      fill: true,
      tension: 0.3,
    },
  ],
});

const fetchThisMonthIncome = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/active/month', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    chartData.value.datasets[0].data = response.data.amounts;

    
  } catch (error) {
    message.error('이번달수입을 불러올 수 없습니다.');
  }
}

onMounted(() => {
  fetchThisMonthIncome();
})
</script>