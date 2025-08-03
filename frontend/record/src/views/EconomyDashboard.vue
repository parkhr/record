<template>
  <a-row>
    <a-col :span="12">
      <Mywallet/>
    </a-col>
    <a-col :span="12">
      <MyWalletList/>
    </a-col>
    <a-col :span="12">
      <ThisWeekIncomeChart/>
    </a-col>
    <a-col :span="12">
      <ThisWeekSpendChart/>
    </a-col>
    <a-col :span="12">
      <ThisMonthIncomeChart/>
    </a-col>
    <a-col :span="12">
      <ThisMonthSpendChart/>
    </a-col>
  </a-row>
</template>
<script setup>
import Mywallet from '@/components/Mywallet.vue';
import MyWalletList from '@/components/MyWalletList.vue';
import ThisMonthIncomeChart from '@/components/ThisMonthIncomeChart.vue';
import ThisMonthSpendChart from '@/components/ThisMonthSpendChart.vue';
import ThisWeekIncomeChart from '@/components/ThisWeekIncomeChart.vue';
import ThisWeekSpendChart from '@/components/thisWeekSpendChart.vue';


</script>