<template>
  <div>
    <ToggleButton
      v-model="visible"
      :onLabel="$t('action_recommendations.action_recommendation_description.toggle.btn.hide')"
      :offLabel="$t('action_recommendations.action_recommendation_description.toggle.btn.show')"
      onIcon="pi pi-eye"
      offIcon="pi pi-eye-slash"
      style="min-width: 90%"
    />
    <Dialog
      v-model:visible="visible"
      modal
      :header="$t('action_recommendations.action_recommendation_description.header')"
      :style="{ width: '80%' }"
    >
      <div v-html="props.description"></div>
    </Dialog>
  </div>
</template>
<script setup>
import { ref } from 'vue'

const props = defineProps(['description'])

const visible = ref(false)
</script>
