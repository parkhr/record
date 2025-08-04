<template>
  <a-card title="최근 리스트" bordered>
    <a-table
      :columns="columns"
      :data-source="balances"
      :row-key="record => record.id"
      size="middle"
      bordered
      :pagination="false"
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
import api from '@/api/axios';
import { message } from 'ant-design-vue';
import { onMounted, ref } from 'vue';

// 내부 상태로 가진 잔액 데이터
const balances = ref([]);

const formatNumber = (num) => num?.toLocaleString() ?? '0';

const columns = [
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

const fetchRecent = async () => {
  try {
    const response = await api.get('/api/economy/dashboard/recent', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    balances.value = response.data;
  } catch (error) {
    message.error('최근리스트를 불러올 수 없습니다.');
  }
}

onMounted(() => {
  fetchRecent();
})
</script>
