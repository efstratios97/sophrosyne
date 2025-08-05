<template>
  <div>
    <ActionRecommendationFormTemplate
      :actionRecommendation="props.actionRecommendation"
      :settings="settings"
      :isUpdate="true"
      @submitActionRecommendation="updateActionRecommendation"
    ></ActionRecommendationFormTemplate>
    <Toast />
  </div>
</template>

<script setup>
import { defineEmits, ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import ActionRecommendationFormTemplate from '@/components/action_recommendation/menu/utils/ActionRecommendationFormTemplate.vue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const settings = ref({ submitting: false })

const emit = defineEmits(['getActionRecommendations'])

const props = defineProps(['actionRecommendation'])

const updateActionRecommendation = (event) => {
  settings.value.submitting = true
  event.allowedApikeys =
    event.allowedApikeysAsObject != undefined
      ? event.allowedApikeysAsObject.map((apikey) => {
          return apikey.apikeyname
        })
      : []
  axiosCore
    .put('/int/user/actionrecommendation/' + props.actionRecommendation.id, event)
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t(
          'action_recommendations.action_recommendation_creation_form.btn.update.toast.success.message'
        ),
        detail: t(
          'action_recommendations.action_recommendation_creation_form.btn.update.toast.success.detail'
        ),
        life: 3000
      })
      settings.value.submitting = false
      emit('getActionRecommendations')
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t(
          'action_recommendations.action_recommendation_creation_form.btn.update.toast.error.message'
        ),
        detail: t(
          'action_recommendations.action_recommendation_creation_form.btn.update.toast.error.detail'
        ),
        life: 3000
      })
      settings.value.submitting = false
    })
}
</script>
