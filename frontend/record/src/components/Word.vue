<template>
  <div>
    <a-modal
      v-model:open="open"
      title="단어 학습"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">그만하기</a-button>
        <!-- <a-button key="submit" type="primary" :loading="loading" @click="handleOk">
          등록
        </a-button> -->
      </template>

      <div style="padding: 20px; text-align: center">
        <!-- 카드 영역 -->
        <div v-if="currentIndex < words.length" style="margin-top: 20px">
          <a-card style="width: 350px; margin: 0 auto; position: relative;">
            <!-- 단어 -->
            <p style="font-size: 22px; font-weight: bold; margin-bottom: 10px;">
              {{ words[currentIndex].word }}
            </p>
            <!-- 예문 -->
            <p style="font-size: 16px; color: #555; margin-bottom: 15px;">
              {{ words[currentIndex].example }}
            </p>
            <!-- 뜻 (클릭해서 토글) -->
            <p
              style="font-size: 18px; font-weight: bold; color: #1677ff; cursor: pointer;"
              @click="showMeaning = !showMeaning"
            >
              {{ showMeaning ? words[currentIndex].meaning : '뜻 보기 👀' }}
            </p>
            <!-- 외웠다고 표시 -->
            <!-- <span
              v-if="learned.includes(words[currentIndex].word)"
              style="position: absolute; top: 10px; right: 15px; color: green; font-weight: bold;"
            >
              ✅ 외웠음
            </span> -->
          </a-card>

          <!-- 버튼 영역 -->
          <div style="display: flex; justify-content: center; margin-top: 20px; gap: 40px;">
            <a-button type="primary" danger @click="handleCheck(true)">O</a-button>
            <a-button type="primary" @click="handleCheck(false)">X</a-button>
          </div>
          <!-- <div style="display: flex; justify-content: center; margin-top: 20px; gap: 40px;">
            <a-button type="dashed" @click="markAsLearned">외웠다 ✅</a-button>
          </div> -->
        </div>

        <!-- 끝났을 때 -->
        <div v-else>
          <p style="margin-top: 30px; font-size: 18px; font-weight: bold;">
            🎉 모든 단어를 확인했습니다!
          </p>
        </div>

        <!-- 통계 -->
        <div style="margin-top: 30px; display: flex; justify-content: center; gap: 40px;">
          <a-statistic title="✅ 외운 단어" :value="learned.length" />
          <a-statistic title="❌ 못 외운 단어" :value="notLearned.length" />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";

const open = ref(false);
const callback = ref<null | Function>(null);

// 단어 + 뜻 + 예문
const words = [
  { word: "earn", meaning: "벌다, 얻다", example: "He earned the respect of his teammates." },
  { word: "admission", meaning: "입학, 입장, 인정", example: "The college admission process is competitive." },
  { word: "outstanding", meaning: "뛰어난, 두드러진", example: "She gave an outstanding performance on stage." },
  { word: "matter", meaning: "문제, 일 / 중요하다", example: "It doesn’t matter to me." },
  { word: "fitness", meaning: "건강, 체력, 적합성", example: "He goes to the gym to improve his fitness." },
  { word: "assessment", meaning: "평가, 판단", example: "The teacher made an assessment of the students’ work." },
  { word: "slightly", meaning: "약간, 조금", example: "She looked slightly tired." },
  { word: "admit", meaning: "인정하다, 입장시키다", example: "He admitted his mistake." },
  { word: "enroll", meaning: "등록하다, 입학하다", example: "She enrolled in a cooking class." },
  { word: "nearly", meaning: "거의, 대략", example: "It’s nearly midnight." },
  { word: "athlete", meaning: "운동선수", example: "The athlete won a gold medal." },
  { word: "remarkable", meaning: "주목할 만한, 놀라운", example: "The invention was a remarkable achievement." },
  { word: "substantial", meaning: "상당한, 중요한", example: "They made a substantial profit last year." },
];

const currentIndex = ref(0);
const learned = ref<string[]>([]);
const notLearned = ref<string[]>([]);
const showMeaning = ref(false);
const loading = ref(false);

const show = (cb?: Function) => {
  callback.value = cb ?? null;
  open.value = true;


};

const handleOk = async () => {
  if (callback.value) {
    callback.value({ learned: learned.value, notLearned: notLearned.value });
  }
  open.value = false;
};

const handleCancel = () => {
  open.value = false;
  currentIndex.value = 0;
  learned.value = [];
  notLearned.value = [];
  showMeaning.value = false;
};

const handleCheck = (isKnown: boolean) => {
  const word = words[currentIndex.value].word;
  if (isKnown) {
    learned.value.push(word);
  } else {
    notLearned.value.push(word);
  }
  currentIndex.value++;
  showMeaning.value = false; // 다음 단어는 뜻 숨김
};

// const markAsLearned = () => {
//   const word = words[currentIndex.value].word;
//   if (!learned.value.includes(word)) {
//     learned.value.push(word);
//   }
// };
defineExpose({ show });
</script>
