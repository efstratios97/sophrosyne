<template>
  <div>
    <ActionFormTemplate
      :action="{}"
      :isDynamicAction="true"
      :settings="settings"
      @submitAction="createNewAction"
    ></ActionFormTemplate>
    <Toast />
  </div>
</template>

<script setup>
import { defineEmits, ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import ActionFormTemplate from '@/components/actions/utils/ActionFormTemplate.vue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const emit = defineEmits(['getDynamicActions'])

const settings = ref({ submitting: false })

const createNewAction = (event) => {
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
    .post('/int/user/dynamicaction', event)
    .then((res) => {
      event.intPassword = res.data
      toast.add({
        severity: 'success',
        summary: t('actions.dynamic_action.action_creation_form.btn.create.toast.success.message'),
        detail: t('actions.dynamic_action.action_creation_form.btn.create.toast.success.detail'),
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
