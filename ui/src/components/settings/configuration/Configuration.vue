<template>
  <div>
    <Card>
      <template #title> {{ $t('settings.configuration.export_config_data.title') }} </template>
      <template #subtitle>
        {{ $t('settings.configuration.export_config_data.sub_title') }}
      </template>
      <template #content>
        <div class="block font-bold text-center p-1 border-round mb-1">
          <Button text raised outlined class="border-2" @click="exportSophrosyneData">
            <img src="@/assets/images/EXPORT_DATA.png" alt="Image" style="max-height: 5vh" />
          </Button>
        </div>
      </template>
    </Card>
    <Toast />
  </div>
</template>
<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const exportSophrosyneData = () => {
  axiosCore
    .get('/int/admin/configuration/export')
    .then((res) => {
      const blob = new Blob([JSON.stringify(res.data, null, 2)], { type: 'application/json' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = 'sophrosyne.json'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      toast.add({
        severity: 'success',
        summary: t('settings.configuration.export_config_data.toast.success.message'),
        detail: t('settings.configuration.export_config_data.toast.success.detail'),
        life: 10000
      })
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.configuration.export_config_data.toast.error.message'),
        detail: t('settings.configuration.export_config_data.toast.error.detail'),
        life: 3000
      })
    })
}
</script>
<style></style>
