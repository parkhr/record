<template>
  <a-card title="잔액 목록" bordered>
    <a-table
      :columns="columns"
      :data-source="balances"
      :row-key="record => record.id"
      size="middle"
      bordered
      pagination={false}
    >
      <template #amount="{ record }">
        <span :style="{ color: record.amount >= 0 ? '#52c41a' : '#f5222d' }">
          ₩ {{ formatNumber(record.amount) }}
        </span>
      </template>

      <template #status="{ record }">
        <a-tag :color="record.amount >= 0 ? 'green' : 'red'">
          {{ record.amount >= 0 ? '적립' : '차감' }}
        </a-tag>
      </template>
    </a-table>
  </a-card>
</template>

<script setup>
import { ref } from 'vue';

// 내부 상태로 가진 잔액 데이터
const balances = ref([
  { id: 1, title: '출근 활동', amount: 10000, date: '2025-08-02 09:30' },
  { id: 2, title: '지각 페널티', amount: -2000, date: '2025-08-02 09:45' },
  { id: 3, title: '청소 참여', amount: 3000, date: '2025-08-02 10:00' },
]);

const formatNumber = (num) => num?.toLocaleString() ?? '0';

const columns = [
  {
    title: '항목',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '잔액',
    key: 'amount',
    slots: { customRender: 'amount' },
  },
  {
    title: '상태',
    key: 'status',
    slots: { customRender: 'status' },
    align: 'center',
  },
  {
    title: '날짜',
    dataIndex: 'date',
    key: 'date',
  }
];
</script>
