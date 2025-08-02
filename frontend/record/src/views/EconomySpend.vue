<template>
  <a-row :gutter="[5, 5]" style="margin-bottom: 20px">
    <a-col :span="2">
      <a-select
        v-model:value="status"
        style="width: 100%"
        :options="statuses.map(pro => ({ value: pro }))"
      ></a-select>
    </a-col>
    <a-col :span="3">
      <a-range-picker v-model:value="insertDateRange" style="width: 300px" />
    </a-col>

    <a-col :span="24"></a-col>

    <a-col :span="1">
      <a-button :icon="h(SearchOutlined)" @click="search" style="width: 100%">Search</a-button>
    </a-col>
    <a-col :span="1">
      <a-button @click="reset" style="width: 100%">Reset</a-button>
    </a-col>
  </a-row>

  <!-- LIST -->
  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="handleTableChange">
    <template #title>
      <a-row :gutter="5" justify="end">
        <a-col>
          <a-button @click="onExport">엑셀 다운로드</a-button>
        </a-col>
        <a-col>
          <a-button type="primary" @click="onCreate">등록</a-button>
        </a-col>
      </a-row>
    </template>
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'amount'">
        {{ Number(record.amount).toLocaleString() }}
      </template>
      <template v-if="column.key === 'action'">
        <span>
          <a v-if="!record.deducted" @click="onDeduct(record)">차감</a>
          <a v-else @click="onCancelDeduct(record)">차감취소</a>
          <a-divider type="vertical" />
          <a @click="onDelete(record)">Delete</a>
        </span>
      </template>
    </template>
  </a-table>

  <SpendCreateModal ref="spendCreateModal" />
  <CommonModal ref="deleteModalRef"/>
  <CommonModal ref="deductedModalRef"/>
  <CommonModal ref="cancelDeductedModalRef"/>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { onMounted, ref, h, computed } from 'vue'
import type { Dayjs } from 'dayjs';
import { SearchOutlined } from '@ant-design/icons-vue';
import SpendCreateModal from '@/components/SpendCreateModal.vue';
import { message } from 'ant-design-vue';
import CommonModal from '@/components/CommonModal.vue';

type RangeValue = [Dayjs, Dayjs];

const searchParams = ref({
  status: '',
  startDate: null,
  endDate: null,
  page: 0,
  pageSize: 20
});
const statuses = ['전체', '차감', '차감취소'];
// 숫자로 변환된 차감 여부
const deductedValue = computed(() => status.value === '차감' ? 1 : 0);
const status = ref(statuses[0]);
const insertDateRange = ref<RangeValue>();

const deleteModalRef = ref();
const deductedModalRef = ref();
const cancelDeductedModalRef = ref();
const spendCreateModal = ref();

const columns = [
  {
    title: 'No',
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: '지출금액',
    dataIndex: 'amount',
    key: 'amount',
  },
  {
    title: '장소',
    dataIndex: 'place',
    key: 'place',
  },
  {
    title: '지출날짜',
    key: 'spendAt',
    dataIndex: 'spendAt',
  },
  {
    title: 'Action',
    key: 'action',
  },
];

const data = ref([]);
const pagination = ref({
  current: 1,
  pageSize: 20,
  position: ['bottomCenter'],
  total: 0,
})

const fetchSpends = async (params) => {
  try {
    const response = await api.get('/api/economy/spend/search', {
      headers: {
        'Content-Type': 'application/json', 
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    const totalElements = response.data.totalElements;
    pagination.value.total = totalElements
    data.value = response.data.content.map((item, index) => {
      item.key = totalElements - (params.page * params.pageSize + index);
      return item;
    });
  } catch (error) {
    console.log(error)
    message.error('지출내역을 불러올 수 없습니다.');
  }
};

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.pageSize = pageSize;

  fetchSpends(searchParams.value)
}

const search = () => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  if (status.value && status.value !== '전체') {
    params.status = deductedValue.value;
  }
  if (insertDateRange.value) {
    params.startDate = insertDateRange.value[0].format('YYYY-MM-DD');
    params.endDate = insertDateRange.value[1].format('YYYY-MM-DD');
  }

  searchParams.value = params;

  fetchSpends(params);
};

const reset = () => {

  status.value = statuses[0];
  insertDateRange.value = null;

  searchParams.value = {
    status: '',
    startDate: null,
    endDate: null,
    page: 0,
    pageSize: pagination.value.pageSize
  };

  pagination.value.current = 1;

  fetchSpends(searchParams.value);
}

const onCreate = () => {
  spendCreateModal.value.show(() => {
    fetchSpends(searchParams.value);
  });
}

const onExport = () => {
  // Logic to handle export functionality
  console.log("Export records to Excel");
};

const onDeduct = (spend) => {

  deductedModalRef.value.show('지출내역차감', '지출내역을 차감하시겠습니까?', 
    async () => {

      try {
        const response = await api.post('/api/economy/minus', {
          spendId: spend.id
        }, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('지출내역이 차감되었습니다.');
      } catch (error) {
        message.error('지출내역 차감 실패');
      }

      fetchSpends(searchParams.value);
    });
} 

const onCancelDeduct = (spend) => {
  cancelDeductedModalRef.value.show('지출내역차감취소', '지출내역 차감을 취소하시겠습니까?', 
    async () => {

      try {
        const response = await api.post('/api/economy/cancel-minus', {
          spendId: spend.id
        }, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('지출내역 차감이 취소되었습니다.');
      } catch (error) {
        message.error('지출내역 차감 취소 실패');
      }

      fetchSpends(searchParams.value);
    });
} 

const onDelete = (spend) => {
  deleteModalRef.value?.show('지출내역삭제', '지출내역을 삭제하시겠습니까?', 
    async () => {

      try {
        const response = await api.delete('/api/economy/spend/' + spend.id, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('지출내역을 삭제되었습니다.');
      } catch (error) {
        message.error('지출내역 삭제 실패');
      }

      fetchSpends(searchParams.value);
    });
}

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchSpends(params);
});
</script>