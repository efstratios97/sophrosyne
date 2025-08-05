<template>
  <div>
    <Card class="sophrosyne-card">
      <template #title> {{ $t('settings.apikeysnuke.title') }} </template>
      <template #subtitle> {{ $t('settings.apikeysnuke.sub_title') }} </template>
      <template #content>
        <div class="block font-bold text-center p-1 border-round mb-1">
          <Button text raised outlined class="border-2" @click="nukeApikeys">
            <img src="@/assets/images/NUKE_APIKEYS.png" alt="Image" style="max-height: 5vh" />
          </Button>
        </div>
      </template>
    </Card>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const nukeApikeys = () => {
  axiosCore
    .put('/int/admin/apikeys/nuke')
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('settings.apikeysnuke.toast.success.message'),
        detail: t('settings.apikeysnuke.toast.success.detail'),
        life: 3000
      })
      setTimeout(function () {
        location.reload()
      }, 1500)
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.apikeysnuke.toast.error.message'),
        detail: t('settings.apikeysnuke.toast.error.detail'),
        life: 3000
      })
    })
}
</script>

<style></style>
