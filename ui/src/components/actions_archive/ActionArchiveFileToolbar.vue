<template>
  <Toolbar class="mb-2">
    <template #start>
      <Button
        :label="$t('actions_archive.toolbar.clipboard.btn')"
        icon="pi pi-copy"
        severity="primary"
        @click="copyTextData"
      />
    </template>
    <template #end
      ><Button
        :label="$t('actions_archive.toolbar.download.btn')"
        icon="pi pi-download"
        severity="primary"
        @click="downloadTextData"
      />
    </template>
  </Toolbar>
</template>
<script setup>
import { defineProps } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const props = defineProps(['textData'])

const copyTextData = () => {
  navigator.clipboard.writeText(props.textData).then(
    function () {
      toast.add({
        severity: 'success',
        summary: t('actions_archive.toolbar.clipboard.toast.success.message'),
        detail: t('actions_archive.toolbar.clipboard.toast.success.detail'),
        life: 3000
      })
    },
    function () {
      toast.add({
        severity: 'error',
        summary: t('actions_archive.toolbar.clipboard.toast.error.message'),
        detail: t('actions_archive.toolbar.clipboard.toast.error.detail'),
        life: 3000
      })
    }
  )
}

const downloadTextData = () => {
  try {
    const file = new File([props.textData], 'sophrosyne_logs_download.txt', {
      type: 'text/plain'
    })
    const link = document.createElement('a')
    const url = URL.createObjectURL(file)

    link.href = url
    link.download = file.name
    document.body.appendChild(link)
    link.click()

    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    toast.add({
      severity: 'success',
      summary: t('actions_archive.toolbar.download.toast.success.message'),
      detail: t('actions_archive.toolbar.download.toast.success.detail'),
      life: 3000
    })
  } catch (exceptionVar) {
    toast.add({
      severity: 'error',
      summary: t('actions_archive.toolbar.download.toast.error.message'),
      detail: t('actions_archive.toolbar.download.toast.error.detail'),
      life: 3000
    })
  }
}
</script>
