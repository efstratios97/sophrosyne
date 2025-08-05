<template>
  <div>
    <Card class="sophrosyne-card">
      <template #header>
        <div class="sophrosyne-center-element">
          <img
            class="border-round w-full"
            src="@/assets/images/ACTION_CONFIRMATION.gif"
            alt=""
            style="max-width: 300px; max-height: 200px"
          />
        </div>
      </template>
      <template #title>{{ props.confirmation.actionName }}</template>
      <template #subtitle>{{
        props.confirmation.triggered_time != null
          ? props.confirmation.triggered_time.substring(
              0,
              props.confirmation.triggered_time.lastIndexOf('.')
            )
          : ''
      }}</template>
      <template #content>
        <p class="m-0 sophrosyne-text-break">
          {{ props.confirmation.actionDescription }}
        </p>
        <p class="p-1 sophrosyne-text-break sophrosyne-code">
          {{ props.confirmation.actionCommand }}
        </p>
      </template>
      <template #footer>
        <div class="flex gap-3 mt-1">
          <Button
            :label="$t('action_confirmation.confirmation_card.btn_reject')"
            severity="danger"
            outlined
            class="w-full"
            @click="confirmAction(false)"
          />
          <Button
            :label="$t('action_confirmation.confirmation_card.btn_confirm')"
            class="w-full"
            @click="confirmAction(true)"
          />
        </div>
      </template>
    </Card>
    <ConfirmDialog :group="confirmation.runningActionId">
      <template #message="slotProps">
        <div
          class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
        >
          <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
          {{ $t('action_confirmation.confirmation_card.explanation') }}
          <p>{{ slotProps.message.message }}</p>
          <ul v-for="action in selectedActions" :key="action" class="list-disc">
            <li>{{ action.name }}</li>
          </ul>
        </div>
      </template>
    </ConfirmDialog>
  </div>
</template>
<script setup>
import { ref, defineProps, inject } from 'vue'
import { useConfirm } from 'primevue/useconfirm'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const props = defineProps(['confirmation'])
const emit = defineEmits(['getActionConfirmationRequests'])

const confirm = useConfirm()
const confirmAction = (confirmed) => {
  var confirmation = ref({ ...props.confirmation })
  confirmation.value.confirmed = confirmed ? 1 : 0
  confirm.require({
    group: confirmation.value.runningActionId,
    header: t('action_confirmation.confirmation_dialog.title'),
    message: confirmed
      ? t('action_confirmation.confirmation_dialog.confirmation.message')
      : t('action_confirmation.confirmation_dialog.rejection.message'),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      axiosCore
        .post('/int/client/confirmation', confirmation.value)
        .then(() => {
          toast.add({
            severity: 'success',
            summary: confirmed
              ? t('action_confirmation.confirmation_dialog.confirmation.toast.success.message')
              : t('action_confirmation.confirmation_dialog.rejection.toast.success.message'),
            detail: confirmed
              ? t('action_confirmation.confirmation_dialog.confirmation.toast.success.detail')
              : t('action_confirmation.confirmation_dialog.rejection.toast.success.detail'),
            life: 3000
          })
          emit('getActionConfirmationRequests')
        })
        .catch(() => {
          toast.add({
            severity: 'error',
            summary: confirmed
              ? t('action_confirmation.confirmation_dialog.confirmation.toast.error.message')
              : t('action_confirmation.confirmation_dialog.rejection.toast.error.message'),
            detail: confirmed
              ? t('action_confirmation.confirmation_dialog.confirmation.toast.error.detail')
              : t('action_confirmation.confirmation_dialog.rejection.toast.error.detail'),
            life: 3000
          })
        })
    },
    reject: () => {}
  })
}
</script>
