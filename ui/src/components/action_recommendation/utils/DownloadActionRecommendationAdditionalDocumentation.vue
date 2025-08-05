<template>
  <div>
    <Button
      type="button"
      :label="
        $t(
          'action_recommendations.action_recommendation_additional_documentation.download.btn.available.label'
        )
      "
      icon="pi pi-download"
      severity="primary"
      @click="downloadAdditionalDocumentation(props.actionRecommendation)"
      class="download-button"
      v-if="props.actionRecommendation.additionalDocumentation != null"
      style="min-width: 90%"
    />
    <Button
      type="button"
      :label="
        $t(
          'action_recommendations.action_recommendation_additional_documentation.download.btn.unavailable.label'
        )
      "
      icon="pi pi-download"
      severity="secondary"
      :disabled="true"
      class="download-button"
      style="min-width: 90%"
      v-else
    />
  </div>
</template>
<script setup>
import { useActionRecommendationComposable } from '@/composables/ActionRecommendationComposable.js'

const { downloadAdditionalDocumentation } = useActionRecommendationComposable()

const props = defineProps(['actionRecommendation'])
</script>
<style scoped>
.download-button {
  min-width: 100%;
}
</style>
