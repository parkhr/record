<template>
  <a-card title="이번달 지출" bordered hoverable>
    <LineChart :chartData="testData" />
  </a-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { LineChart } from 'vue-chart-3';
import { Chart, registerables } from "chart.js";

Chart.register(...registerables);

export default defineComponent({
  name: 'Home',
  components: { LineChart },
  setup() {
    const testData = {
      labels: [
        '1', '2', '3', '4', '5', 
        '6', '7', '8', '9', '10', 
        '11', '12', '13', '14', '15', 
        '16', '17', '18', '19', '20',
        '21', '22', '23', '24', '25',
        '26', '27', '28', '29', '30'],
      datasets: [
        {
          label: '지출',
          data: [30, 40, 60, 70, 5,
            30, 40, 60, 70, 5,
            30, 40, 60, 70, 5,
            30, 40, 60, 70, 5,
            30, 40, 60, 70, 5,
            30, 40, 60, 70, 5
          ],
          // backgroundColor: ['#77CEFF', '#0079AF', '#123E6B', '#97B0C4', '#A5C8ED'],
        },
      ],
    };

    return { testData };
  },
});
</script>