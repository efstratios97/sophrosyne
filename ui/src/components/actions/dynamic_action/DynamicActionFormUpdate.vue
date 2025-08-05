<template>
  <div>
    <ActionFormTemplate
      :action="props.action"
      :isDynamicAction="true"
      :settings="settings"
      @submitAction="updateAction"
    ></ActionFormTemplate>
    <Toast />
  </div>
</template>

<script setup>
import { defineEmits, ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import ActionFormTemplate from '@/components/actions/utils/ActionFormTemplate.vue'

const props = defineProps(['action'])
const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const emit = defineEmits(['getActions'])
const settings = ref({ submitting: false })

const updateAction = (event) => {
  settings.value.submitting = true
  event.allowedApikeys =
    event.allowedApikeysAsObject != undefined
      ? event.allowedApikeysAsObject.map((apikey) => {
          return apikey.apikeyname
        })
      : []
  event.requiresConfirmation = event.requiresConfirmation ? 1 : 0
  event.keepLatestConfirmationRequest = event.keepLatestConfirmationRequest ? 1 : 0
  event.muted = event.muted ? 1 : 0
  axiosCore
    .put('/int/user/dynamicaction/' + event.id, event)
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('actions.dynamic_action.action_creation_form.btn.update.toast.success.message'),
        detail: t('actions.dynamic_action.action_creation_form.btn.update.toast.success.detail'),
        life: 3000
      })
      settings.value.submitting = false
      emit('getDynamicActions')
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('actions.dynamic_action.action_creation_form.btn.create.toast.error.message'),
        detail: t('actions.dynamic_action.action_creation_form.btn.create.toast.error.detail'),
        life: 3000
      })
      settings.value.submitting = false
    })
}
</script>

<style></style>
