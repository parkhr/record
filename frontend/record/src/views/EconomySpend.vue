<template>
  <a-row :gutter="[5, 5]" style="margin-bottom: 20px">
    <a-col :span="4">
      <a-input v-model:value="title" style="width: 100%" placeholder="" />
    </a-col>
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
      <template v-if="column.key === 'title'">
        <a>
          {{ record.title }}
        </a>
      </template>
      <template v-else-if="column.key === 'action'">
        <span>
          <a v-if="!record.deducted" @click="onDeduct(record)">차감</a>
          <a v-else @click="onCancelDeduct(record)">차감취소</a>
        </span>
      </template>
    </template>
  </a-table>

  <SpendCreateModal ref="spendCreateModal" />
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { onMounted, ref, h } from 'vue'
import type { Dayjs } from 'dayjs';
import { SearchOutlined } from '@ant-design/icons-vue';
import SpendCreateModal from '@/components/SpendCreateModal.vue';
import { message } from 'ant-design-vue';

type RangeValue = [Dayjs, Dayjs];

const title = ref('');
const searchParams = ref({
  page: 0,
  pageSize: 20
});
const statuses = ['전체', 'TEMP', 'REGISTER', 'DELETE'];
const status = ref(statuses[0]);
const insertDateRange = ref<RangeValue>();

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

const spendCreateModal = ref();

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

const fetchRoles = async (params) => {
  try {
    const response = await api.get('/api/role/search', {
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
    console.error("Error fetching records:", error);
    // message.error('권한그룹을 불러올 수 없습니다.');
  }
};

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.pageSize = pageSize;

  fetchRoles(searchParams.value)
}

const search = () => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  if (title.value) {
    params.title = title.value;
  }
  if (status.value && status.value !== '전체') {
    params.status = status.value;
  }
  if (insertDateRange.value) {
    params.startDate = insertDateRange.value[0].format('YYYY-MM-DD');
    params.endDate = insertDateRange.value[1].format('YYYY-MM-DD');
  }

  searchParams.value = params;

  fetchRoles(params);
};

const reset = () => {
  searchParams.value = {
    page: 0,
    pageSize: pagination.value.pageSize
  };

  pagination.value.current = 1;

  fetchRoles(searchParams.value);
}

const onCreate = () => {
  spendCreateModal.value.show(() => {

    // fetchRoles(searchParams.value);
  });
}

const onExport = () => {
  // Logic to handle export functionality
  console.log("Export records to Excel");
};

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchSpends(params);
});
</script>