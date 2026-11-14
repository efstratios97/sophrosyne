<template>
  <footer class="flex justify-content-center align-items-center py-1">
    <small class="text-color-secondary"
      >&copy; {{ currentYear }}
      <a href="#" @click.prevent="toggleEasterEgg">{{ $t('copyright.company') }}</a
      >{{ $t('copyright.rights') }}
      <a href="https://docs.sophrosyne.ep-monitoring.com/#/" target="_blank">{{
        $t('copyright.docs')
      }}</a
      >&nbsp;<a href="https://github.com/efstratios97/sophrosyne" target="_blank">{{
        $t('copyright.repo')
      }}</a></small
    >
  </footer>
  <Dialog
    v-model:visible="showEasterEgg"
    modal
    maximizable
    header="A note from the author"
    @close="toggleEasterEgg"
    class="easter-egg-dialog"
  >
    <div class="easter-egg-message">
      <p class="line-height-3">
        My name is Efstratios Pahis, and I built this tool in my freetime during evenings, weekends
        and holidays to help automate the operations of my employer as I saw fit.
      </p>
      <p class="line-height-3">
        Hope it saves you time, made a task easier, or quietly keeps things running.
      </p>
      <p class="line-height-3">
        Want help building a Monitoring System, integrating Sophrosyne and Automating your
        Operations...? Contact me here:
        <a
          href="mailto:sophrosyne@ep-monitoring.com"
          class="font-bold no-underline text-amber-700 hover:underline"
        >
          sophrosyne@ep-monitoring.com
        </a>
      </p>
      <p class="line-height-3 mt-2">— Efstratios</p>
    </div>
    <img
      :src="easterEggImage"
      alt="Easter egg"
      class="easter-egg-img block mx-auto border-round shadow-2"
    />
  </Dialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import { Dialog } from 'primevue'
import easterEggImage from '@/assets/images/EASTER_EGG.jpg'

const currentYear = computed(() => new Date().getFullYear())

const showEasterEgg = ref(false)

const toggleEasterEgg = () => {
  showEasterEgg.value = !showEasterEgg.value
}
</script>

<style scoped>
.easter-egg-dialog :deep(.p-dialog-content) {
  display: flex;
  justify-content: center;
  overflow: hidden;
}

.easter-egg-img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}
</style>
