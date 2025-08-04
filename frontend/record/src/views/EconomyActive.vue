<template>
  <a-row :gutter="[10, 10]" style="margin-bottom: 20px">
    <a-col :span="19">
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
    </a-col>

    <a-col :span="4">
      <Stopwatch @refreshByChild="refreshByChild"/>
    </a-col>
    <a-col :span="1">
      
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
      <template v-if="column.key === 'action'">
        <span>
          <a v-if="!record.saved" @click="onSave(record)">적립</a>
          <a v-else @click="onCancelSave(record)">적립취소</a>
          <a-divider type="vertical" />
          <a @click="onDelete(record)">Delete</a>
        </span>
      </template>
    </template>
  </a-table>
  <CommonModal ref="deleteModalRef"/>
  <CommonModal ref="savedModalRef"/>
  <CommonModal ref="cancelSavedModalRef"/>
  
  <ActiveCreateModal ref="activeCreateModalRef"/>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { onMounted, ref, h, computed } from 'vue'
import type { Dayjs } from 'dayjs';
import { SearchOutlined } from '@ant-design/icons-vue';
import CommonModal from '@/components/CommonModal.vue';
import { message } from 'ant-design-vue';
import Stopwatch from '@/components/Stopwatch.vue';
import ActiveCreateModal from '@/components/ActiveCreateModal.vue';
type RangeValue = [Dayjs, Dayjs];

const searchParams = ref({
  status: '',
  startDate: '',
  endDate: '',
  page: 0,
  pageSize: 20
});
const statuses = ['전체', '적립', '적립취소'];
const savedValue = computed(() => status.value === '적립' ? 1 : 0);
const status = ref(statuses[0]);
const insertDateRange = ref<RangeValue>();

const deleteModalRef = ref();
const savedModalRef = ref();
const cancelSavedModalRef = ref();
const activeCreateModalRef = ref();

const columns = [
  {
    title: 'No',
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: '활동시간 (분)',
    dataIndex: 'minutes',
    key: 'minutes',
  },
  {
    title: '등록일',
    key: 'createdAt',
    dataIndex: 'createdAt',
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

const fetchActives = async (params) => {
  try {
    const response = await api.get('/api/economy/active/search', {
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
    message.error('활동내역을 불러올 수 없습니다.');
  }
};

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.pageSize = pageSize;

  fetchActives(searchParams.value)
}

const search = () => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  if (status.value && status.value !== '전체') {
    params.status = savedValue.value;
  }
  if (insertDateRange.value) {
    params.startDate = insertDateRange.value[0].format('YYYY-MM-DD');
    params.endDate = insertDateRange.value[1].format('YYYY-MM-DD');
  }

  searchParams.value = params;

  fetchActives(params);
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
  fetchActives(searchParams.value);
}

const onCreate = () => {
  activeCreateModalRef.value.show(() => {
    fetchActives(searchParams.value);
  });
}

const onExport = () => {
  // Logic to handle export functionality
  console.log("Export records to Excel");
};

const onSave = (active) => {

  savedModalRef.value.show('활동내역적립', '활동내역을 적립하시겠습니까?', 
    async () => {

      try {
        const response = await api.post('/api/economy/plus', {
          activeId: active.id
        }, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('활동내역이 적립되었습니다.');
      } catch (error) {
        message.error('활동내역 적립 실패');
      }

      fetchActives(searchParams.value);
    });
} 

const onCancelSave = (active) => {
  cancelSavedModalRef.value.show('활동내역적립취소', '활동내역 적립을 취소하시겠습니까?', 
    async () => {

      try {
        const response = await api.post('/api/economy/cancel-plus', {
          activeId: active.id
        }, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('활동내역 적립이 취소되었습니다.');
      } catch (error) {
        message.error('활동내역 적립 취소 실패');
      }

      fetchActives(searchParams.value);
    });
} 

const onDelete = (active) => {
  deleteModalRef.value?.show('활동내역삭제', '활동내역을 삭제하시겠습니까?', 
    async () => {

      try {
        const response = await api.delete('/api/economy/active/' + active.id, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        message.success('활동내역을 삭제되었습니다.');
      } catch (error) {
        message.error('활동내역 삭제 실패');
      }

      fetchActives(searchParams.value);
    });
}

const refreshByChild = () => {

  fetchActives(searchParams.value);
}

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchActives(params);
});
</script>