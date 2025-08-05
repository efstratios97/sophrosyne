<template>
  <div>
    <Card class="sophrosyne-card">
      <template #title> {{ $t('settings.apikeysdisplay.title') }} </template>
      <template #subtitle> {{ $t('settings.apikeysdisplay.sub_title') }} </template>
      <template #content>
        <Carousel :value="apikeys" :numVisible="3" :numScroll="1">
          <template #item="slotProps">
            <div class="border-1 surface-border border-round m-1 text-center py- px-3">
              <div class="mt-2 mb-1">
                <img
                  src="@/assets/images/APIKEYS.png"
                  alt="Image"
                  class="shadow-2"
                  style="max-height: 8vh"
                />
              </div>
              <div>
                <h4 class="mb-1">{{ slotProps.data.apikeyname }}</h4>
                <h6 class="mt-0 mb-1" style="word-wrap: break-word">
                  {{ slotProps.data.apikeydescription }}
                </h6>
                <i v-if="slotProps.data.apikeyactive" class="pi pi-check-circle"></i>
                <i v-else class="pi pi-times-circle"></i>
                <div class="mt-2 mb-2">
                  <Button
                    v-tooltip="{ value: 'Reveal Apikey', showDelay: 100, hideDelay: 300 }"
                    icon="pi pi-eye"
                    rounded
                    severity="info"
                    class="mr-2"
                    @click="revealApikey(slotProps.data.apikey)"
                  />
                  <Button
                    icon="pi pi-check-circle"
                    v-tooltip="{ value: 'Activate Apikey', showDelay: 100, hideDelay: 300 }"
                    rounded
                    severity="success"
                    class="mr-2"
                    @click="activateApikey(slotProps.data.apikeyname)"
                  />
                  <Button
                    icon="pi pi-times-circle"
                    v-tooltip="{ value: 'Deactivate Apikey', showDelay: 100, hideDelay: 300 }"
                    rounded
                    severity="warn"
                    class="mr-2"
                    @click="deactivateApikey(slotProps.data.apikeyname)"
                  />
                  <Button
                    icon="pi pi-trash"
                    v-tooltip="{ value: 'Delete Apikey', showDelay: 100, hideDelay: 300 }"
                    rounded
                    severity="danger"
                    @click="deleteApikey(slotProps.data.apikeyname)"
                  />
                </div>
              </div>
            </div>
          </template>
        </Carousel>
      </template>
    </Card>
    <Dialog
      v-model:visible="toggleRevealApikey"
      modal
      maximizable
      :header="$t('settings.apikey_reveal.title')"
      v-if="toggleRevealApikey"
      @close="revealApikey"
    >
      <Panel :header="$t('settings.apikey_reveal.fieldset_header')" toggleable>
        <h4 class="white-space-normal" style="word-wrap: break-word">
          {{ currentRevealedApikey }}
        </h4>
      </Panel>
    </Dialog>
    <Toast />
  </div>
</template>
<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useApikeysComposable } from '@/composables/ApikeysComposable.js'
import { Dialog } from 'primevue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const { getApikeys, apikeys } = useApikeysComposable()

onMounted(() => {
  getApikeys()
})

const toggleRevealApikey = ref(false)
const currentRevealedApikey = ref('')
const revealApikey = (apikey) => {
  currentRevealedApikey.value = apikey
  toggleRevealApikey.value = !toggleRevealApikey.value
}

const activateApikey = (apikeyname) => {
  axiosCore
    .put('/int/admin/apikey/' + apikeyname + '/activate')
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('settings.apikeysdisplay.activate_toast.success.message'),
        detail: t('settings.apikeysdisplay.activate_toast.success.detail'),
        life: 3000
      })
      getApikeys()
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.apikeysdisplay.activate_toast.error.message'),
        detail: t('settings.apikeysdisplay.activate_toast.error.detail'),
        life: 3000
      })
    })
}

const deactivateApikey = (apikeyname) => {
  axiosCore
    .put('/int/admin/apikey/' + apikeyname + '/deactivate')
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('settings.apikeysdisplay.deactivate_toast.success.message'),
        detail: t('settings.apikeysdisplay.deactivate_toast.success.detail'),
        life: 3000
      })
      getApikeys()
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.apikeysdisplay.deactivate_toast.error.message'),
        detail: t('settings.apikeysdisplay.deactivate_toast.error.detail'),
        life: 3000
      })
    })
}

const deleteApikey = (apikeyname) => {
  axiosCore
    .delete('/int/admin/apikey/' + apikeyname)
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('settings.apikeysdisplay.delete_toast.success.message'),
        detail: t('settings.apikeysdisplay.delete_toast.success.detail'),
        life: 3000
      })
      getApikeys()
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.apikeysdisplay.delete_toast.error.message'),
        detail: t('settings.apikeysdisplay.delete_toast.error.detail'),
        life: 3000
      })
    })
}
</script>
<style></style>
