<template>  
  <div>
    <a-button @click="changeLang('ko')">한국어</a-button>
    <a-button @click="changeLang('en')">English</a-button>
    <a-button @click="changeLang('zh')">中文</a-button>
  </div>
  <a-card>
    <!-- Step 표시 -->
    <a-steps :current="step - 1" size="small" class="mb-6">
      <a-step @click="moveStep(index)" v-for="(s, index) in steps" :key="index" :title="$t(s.title)" />
    </a-steps>

    <a-row style="margin-top: 20px" :gutter="[5, 5]">
      <a-col>
        <a-button @click="prevStep" class="w-full sm:w-auto" :disabled="step === 1">
          {{ $t('button.prev') }}
        </a-button>
      </a-col>
      <a-col>
        <a-button
          type="primary"
          @click="step === steps.length ? saveData() : nextStep()"
          class="w-full sm:w-auto"
        >
          {{ step === steps.length ? $t('button.save') : $t('button.next') }}
        </a-button>
      </a-col>
    </a-row>
    
    <div v-if="step === 1">
      <a-form layout="vertical" style="margin-top: 20px">
        <a-form-item :label="$t('form.label.application')">
          <a-radio-group v-model:value="application">
            <a-row :gutter="[5, 5]">
              <a-col>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.1')">
                  <a-radio value="1">{{ $t('form.value.application.1') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.2')">
                  <a-radio value="2">{{ $t('form.value.application.2') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.3')">
                  <a-radio value="3">{{ $t('form.value.application.3') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.4')">
                  <a-radio value="4">{{ $t('form.value.application.4') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.5')">
                  <a-radio value="5">{{ $t('form.value.application.5') }}</a-radio>
                </a-tooltip>
              </a-col>
            </a-row>
            <a-row :gutter="[5, 5]" style="margin-top: 10px">
              <a-col>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.6')">                
                  <a-radio value="6">{{ $t('form.value.application.6') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.7')">
                  <a-radio value="7">{{ $t('form.value.application.7') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.8')">
                  <a-radio value="8">{{ $t('form.value.application.8') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.9')">
                  <a-radio value="9">{{ $t('form.value.application.9') }}</a-radio>
                </a-tooltip>
                <a-tooltip placement="topLeft" :arrow="mergedArrow" :title="$t('form.tooltip.application.10')">
                  <a-radio value="10">{{ $t('form.value.application.10') }}</a-radio>
                </a-tooltip>
              </a-col>
            </a-row>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="application === '4' || application === '5' || application === '6'" :label="$t('form.label.desiredStatus')">
          <a-row>
            <a-col :span="12">
              <a-input v-model:value="form.name" placeholder="희망 자격을 입력하세요" />
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="application === '8'" :label="$t('form.label.singleOrMultiple')">
          <a-radio-group v-model:value="application">
            <a-row :gutter="[5, 5]">
              <a-col>
                <a-radio value="1">{{ $t('form.value.application.11') }}</a-radio>
                <a-radio value="2">{{ $t('form.value.application.12') }}</a-radio>
              </a-col>
            </a-row>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </div>

    <div v-if="step === 2">
      <a-form layout="vertical" style="margin-top: 20px">
        <!-- 성명 -->
        <a-form-item :label="$t('form.label.name')">
          <a-row :gutter="[5, 5]">
            <a-col :span="3">
              <a-input v-model:value="form.name" placeholder="성" />
            </a-col>
            <a-col :span="3">
              <a-input v-model:value="form.name" placeholder="명" />
            </a-col>
          </a-row>
        </a-form-item>
        
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <!-- 생년월일 -->
            <a-form-item :label="$t('form.label.birth')">
              <a-date-picker
                v-model:value="form.birth"
                format="YYYY-MM-DD"
                style="width: 80%"
                placeholder="생년월일 선택"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <!-- 성별 -->
            <a-form-item :label="$t('form.label.gender')">
              <a-radio-group v-model:value="form.gender">
                <a-radio value="남">남</a-radio>
                <a-radio value="여">여</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <!-- 국적 -->
            <a-form-item :label="$t('form.label.nationality')">
              <a-input v-model:value="form.nationality" placeholder="국적 입력" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 외국인등록번호 -->
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.alienregNo')">
              <a-input v-model:value="form.foreignerId" placeholder="외국인등록번호 입력" />
              <small>예: 홍길동</small>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <!-- 여권번호 -->
            <a-form-item :label="$t('form.label.passportNo')">
              <a-input v-model:value="form.passportNo" placeholder="여권번호 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <!-- 여권 발급일자 -->
            <a-form-item :label="$t('form.label.passportIssue')">
              <a-date-picker
                v-model:value="form.passportIssued"
                format="YYYY-MM-DD"
                style="width: 80%"
                placeholder="여권 발급일자 선택"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <!-- 여권 유효기간 -->
            <a-form-item :label="$t('form.label.passportExpiry')">
              <a-date-picker
                v-model:value="form.passportValid"
                format="YYYY-MM-DD"
                style="width: 80%"
                placeholder="여권 유효기간 선택"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[5, 5]">
          <!-- 대한민국 내 주소 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.koreaAddress')">
              <a-input v-model:value="form.addressKR" placeholder="주소 입력" />
            </a-form-item>
          </a-col>
          <!-- 전화번호 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.tel')">
              <a-input v-model:value="form.phoneKR" placeholder="전화번호 입력" />
            </a-form-item>
          </a-col>
          <!-- 휴대전화 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.mobile')">
              <a-input v-model:value="form.mobileKR" placeholder="휴대전화 입력" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="[5, 5]">
          <!-- 본국 주소 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.homeArress')">
              <a-input v-model:value="form.addressHome" placeholder="본국 주소 입력" />
            </a-form-item>
          </a-col>
          <!-- 본국 전화번호 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.homeTel')">
              <a-input v-model:value="form.phoneHome" placeholder="본국 전화번호 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.email')">
              <a-input v-model:value="form.phoneHome" placeholder="전자우편 입력" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div v-if="step === 3">
      <a-form layout="vertical" style="margin-top: 20px">
        <a-row :gutter="[5, 5]">
          <!-- 추가된 학교/교육 항목 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.isEnrolled')">
              <a-radio-group v-model:value="form.enrolled">
                <a-radio value="1">재학</a-radio>
                <a-radio value="2">미재학</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <!-- 본국 전화번호 -->
          <a-col :span="6">
            <a-form-item :label="$t('form.label.schoolName')">
              <a-input v-model:value="form.schoolName" placeholder="학교 이름 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.schoolTel')">
              <a-input v-model:value="form.schoolPhone" placeholder="학교 전화번호 입력" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.schoolType')">
              <a-input v-model:value="form.schoolType" placeholder="학교 종류 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.eduApproved')">
              <a-radio-group v-model:value="form.enrolled">
                <a-radio value="1">교육청 인가</a-radio>
                <a-radio value="2">교육청 비인가, 대안학교</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form> 
    </div>

    <div v-if="step === 4">
      <a-form layout="vertical" style="margin-top: 20px">
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.workplaceOriginal')">
              <a-input v-model:value="form.workplaceOriginal" placeholder="원 근무처 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.bizNoOriginal')">
              <a-input v-model:value="form.businessNoOriginal" placeholder="사업자등록번호 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.telOriginal')">
              <a-input v-model:value="form.phoneOriginal" placeholder="전화번호 입력" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.workplaceFuture')">
              <a-input v-model:value="form.workplaceFuture" placeholder="예정 근무처 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.bizNoFuture')">
              <a-input v-model:value="form.businessNoFuture" placeholder="사업자등록번호 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.telFuture')">
              <a-input v-model:value="form.phoneFuture" placeholder="전화번호 입력" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.annualIncome')">
              <a-input v-model:value="form.annualIncome" placeholder="연 소득금액 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :label="$t('form.label.job')">
              <a-input v-model:value="form.job" placeholder="직업 입력" />
            </a-form-item>
          </a-col>
          <a-col :span="6">

          </a-col>
        </a-row>
      </a-form> 
    </div>

    <div v-if="step === 5">
      <a-form layout="vertical" style="margin-top: 20px">
        <a-row :gutter="[5, 5]">
          <a-col :span="6">
            <a-form-item :label="$t('form.label.reentryPeriod')">
              <a-date-picker
                v-model:value="form.reIn"
                format="YYYY-MM-DD"
                style="width: 80%"
                placeholder="재입국 신청기간 입력"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
  </a-card>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

const application = ref(0);

// Step 정의
const steps = [
  { title: 'steps.application' },
  { title: 'steps.basic' },
  { title: 'steps.enrollment' },
  { title: 'steps.workplace' },
  { title: 'steps.reentry' }
]
const step = ref(1);

// form 초기값 생성
const form = ref({});

const nextStep = () => { if (step.value < steps.length) step.value++; };
const prevStep = () => { if (step.value > 1) step.value--; };

const moveStep = (clickStep) => {

  step.value = clickStep + 1;
}

const saveData = () => {
  console.log('최종 데이터:', form.value);
  // TODO: HWPX 파일 생성 API 호출
};

function changeLang(lang) {
  locale.value = lang  // 'ko' 또는 'en'
}
</script>