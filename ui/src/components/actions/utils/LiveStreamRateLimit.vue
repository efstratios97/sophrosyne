<template>
  <Card class="sophrosyne-card">
    <template #subtitle>
      {{ $t('control_panel_view.action_executor.rate_limit.sub_title') }}
    </template>
    <template #content>
      <InputGroup>
        <InputNumber
          v-model="rateLimit"
          inputId="integeronly"
          @update:modelValue="updateRateLimit"
        />
        <InputGroupAddon>
          <Checkbox v-model="isActive" :binary="true" @change="updateRateLimit" />
        </InputGroupAddon>
      </InputGroup>
    </template>
  </Card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
const rateLimit = ref(5000)
const isActive = ref(true)

const emits = defineEmits(['updateRateLimit'])

onMounted(() => {
  emits('updateRateLimit', { rateLimit: rateLimit.value, isActive: isActive.value })
})

const updateRateLimit = () => {
  emits('updateRateLimit', { rateLimit: rateLimit.value, isActive: isActive.value })
}
</script>
