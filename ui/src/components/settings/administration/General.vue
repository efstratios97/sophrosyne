<template>
  <div>
    <Card>
      <template #title>
        {{ $t('settings.administration.general.purge_action_archive.title') }}
      </template>
      <template #subtitle>
        {{ $t('settings.administration.general.purge_action_archive.sub_title') }}
      </template>
      <template #content>
        <div class="block font-bold text-center p-1 border-round mb-1">
          <Button text raised outlined class="border-2" @click="purgeActionArchive">
            <img
              src="@/assets/images/PURGE_ACTION_ARCHIVE.png"
              alt="Image"
              style="max-height: 5vh"
            />
          </Button>
        </div>
      </template>
    </Card>
    <Toast />
    <ConfirmDialog group="purgeActionArchive">
      <template #message="slotProps">
        <div
          class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
        >
          <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
          <p>{{ slotProps.message.message }}</p>
          <ul v-for="action in selectedActionRecommendations" :key="action" class="list-disc">
            <li>{{ action.name }}</li>
          </ul>
        </div>
      </template>
    </ConfirmDialog>
  </div>
</template>
<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const confirm = useConfirm()
const purgeActionArchive = () => {
  confirm.require({
    group: 'purgeActionArchive',
    header: t('settings.administration.general.purge_action_archive.confirmation_dialog.title'),
    message: t(
      'settings.administration.general.purge_action_archive.confirmation_dialog.confirmation.message'
    ),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      axiosCore
        .delete('/int/admin/archive/actions')
        .then(() => {
          toast.add({
            severity: 'success',
            summary: t(
              'settings.administration.general.purge_action_archive.confirmation_dialog.confirmation.toast.success.message'
            ),
            detail: t(
              'settings.administration.general.purge_action_archive.confirmation_dialog.confirmation.toast.success.detail'
            ),
            life: 3000
          })
        })
        .catch(() => {
          toast.add({
            severity: 'error',
            summary: t(
              'settings.administration.general.purge_action_archive.confirmation_dialog.confirmation.toast.error.message'
            ),
            detail: t(
              'settings.administration.general.purge_action_archive.confirmation_dialog.confirmation.toast.error.detail'
            ),
            life: 3000
          })
        })
    },
    reject: () => {}
  })
}
</script>
