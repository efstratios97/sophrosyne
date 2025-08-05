<template>
  <div>
    <Card class="sophrosyne-card">
      <template #title> {{ $t('settings.apikeyscreation.title') }} </template>
      <template #subtitle> {{ $t('settings.apikeyscreation.sub_title') }} </template>
      <template #content>
        <div class="block font-bold text-center p-1 border-round mb-1">
          <Button text raised outlined class="border-2" @click="toggleAddApikey">
            <img src="@/assets/images/CREATE_APIKEYS.png" alt="Image" style="max-height: 5vh" />
          </Button>
        </div>
      </template>
    </Card>
    <Dialog
      v-model:visible="showAddApikey"
      modal
      maximizable
      :header="$t('settings.apikeyscreationaddform.modal_header')"
      v-if="showAddApikey"
      @close="toggleAddApikey"
    >
      <ApikeysCreationAddForm @close="toggleAddApikey" />
    </Dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Dialog } from 'primevue'
import ApikeysCreationAddForm from '@/components/settings/apikeys/ApikeysCreationAddForm.vue'

const showAddApikey = ref(false)
const toggleAddApikey = () => {
  showAddApikey.value = !showAddApikey.value
}
</script>

<style></style>
