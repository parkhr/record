<template>
  <a-row>
    <a-col :span="12">
      현재잔고
      <Mywallet/>
    </a-col>
    <a-col :span="12">
      이번주수입
      <Mywallet/>
    </a-col>
    <a-col :span="12">
      이번주 지출
      <Mywallet/>
    </a-col>
    <a-col :span="12">
      한달 수입
      <Mywallet/>
    </a-col>
    <a-col :span="12">
      한달 지출
      <Mywallet/>
    </a-col>
  </a-row>
</template>
<script setup>
import Mywallet from '@/components/Mywallet.vue';


</script>