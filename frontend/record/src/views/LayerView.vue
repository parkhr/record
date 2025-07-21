<script lang="ts" setup>
import type { TreeProps } from 'ant-design-vue';
import { UserOutlined } from '@ant-design/icons-vue';
import { computed, reactive, ref, watch } from 'vue';

import type { Dayjs } from 'dayjs';
type RangeValue = [Dayjs, Dayjs];

const checked = ref(false);



const provinceData = ['Zhejiang', 'Jiangsu'];
const cityData = {
  Zhejiang: ['Hangzhou', 'Ningbo', 'Wenzhou'],
  Jiangsu: ['Nanjing', 'Suzhou', 'Zhenjiang'],
};
const province = ref(provinceData[0]);
const secondCity = ref(cityData[province.value][0]);
const cities = computed(() => {
  return cityData[province.value];
});

watch(province, val => {
  secondCity.value = cityData[val][0];
});




const value1 = ref<RangeValue>();

const current = ref(49);

const expandedKeys = ref<string[]>(['0-0', '0-1']);
const selectedKeys = ref<string[]>([]);
const treeData: TreeProps['treeData'] = [
  {
    title: '컬렉션1',
    key: '0-0',
    children: [
      {
        title: '기록물1',
        key: '0-0-0',
        isLeaf: true,
      },
      {
        title: '기록물2',
        key: '0-0-1',
        isLeaf: true,
      },
      {
        title: '시리즈1',
        key: '0-0-2',
        children: [
          {
            title: '기록물3',
            key: '0-0-2-1',
            isLeaf: true,
          },
          {
            title: '기록물4',
            key: '0-0-2-2',
            isLeaf: true,
          },
          {
            title: '폴더1',
            key: '0-0-2-3',
            children: [
              {
                title: '기록물5',
                key: '0-0-2-3-1',
                isLeaf: true,
              },
              {
                title: '기록물6',
                key: '0-0-2-3-2',
                isLeaf: true,
              },
            ],
          },
        ],
      },
      {
        title: '시리즈2',
        key: '0-0-3',
        children: [
          {
            title: '기록물7',
            key: '0-0-3-1',
            isLeaf: true,
          },
          {
            title: '기록물8',
            key: '0-0-3-2',
            isLeaf: true,
          },
        ],
      }
    ],
  },
  {
    title: '컬렉션2',
    key: '0-1',
    children: [
      {
        title: '기록물',
        key: '0-1-0',
        isLeaf: true,
      },
      {
        title: '기록물',
        key: '0-1-1',
        isLeaf: true,
      },
    ],
  },
];
</script>

<template>
  <a-row>
    <a-col :span="12">
      <a-skeleton active />
    </a-col>
    <a-col :span="12">
      <a-checkbox v-model:checked="checked">Checkbox</a-checkbox>
    </a-col>
  </a-row>
  <a-row>
    <a-col :span="8">
      <a-button type="primary" @click="showModal">Open Modal with async logic</a-button>
    </a-col>
    <a-col :span="8">
      <a-space>
        <a-select
          v-model:value="province"
          style="width: 120px"
          :options="provinceData.map(pro => ({ value: pro }))"
        ></a-select>
        <a-select
          v-model:value="secondCity"
          style="width: 120px"
          :options="cities.map(city => ({ value: city }))"
        ></a-select>
      </a-space>
    </a-col>
    <a-col :span="8">
      <a-space direction="vertical" :size="12">
        <a-range-picker v-model:value="value1" />
      </a-space>
    </a-col>
  </a-row>
  <a-row>
    <a-col :span="3">
      <a-directory-tree
        v-model:expandedKeys="expandedKeys"
        v-model:selectedKeys="selectedKeys"
        multiple
        :tree-data="treeData">
      </a-directory-tree>
    </a-col>
    <a-col :span="3">
      
    </a-col>
    <a-col :span="3">
      
    </a-col>
  </a-row>
  <a-row>
    <a-col :span="24">
      <a-avatar :size="64">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar size="large">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar>
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar size="small">
        <template #icon><UserOutlined /></template>
      </a-avatar>
    
      <a-avatar shape="square" :size="64">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar shape="square" size="large">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar shape="square">
        <template #icon><UserOutlined /></template>
      </a-avatar>
      <a-avatar shape="square" size="small">
        <template #icon><UserOutlined /></template>
      </a-avatar>
    </a-col>
  </a-row>
  <a-modal v-model:open="open" title="Title" :confirm-loading="confirmLoading" @ok="handleOk">
    <p>{{ modalText }}</p>
  </a-modal>
</template>
