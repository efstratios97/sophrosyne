<template>
  <DataView v-if="userLoggedEmbedded" :value="confirmationRequests" :layout="layout">
    <template #header>
      <div class="flex justify-content-end">
        <DataViewLayoutOptions v-model="layout" />
      </div>
    </template>

    <template #list="slotProps">
      <div class="grid grid-nogutter w-full">
        <div v-for="(item, index) in slotProps.items" :key="index" class="col-12">
          <ConfirmationCard
            :confirmation="item"
            @getActionConfirmationRequests="getConfirmationRequiringActions()"
          ></ConfirmationCard>
          <br />
        </div>
      </div>
    </template>

    <template #grid="slotProps">
      <div class="grid grid-nogutter w-full">
        <div v-for="(item, index) in slotProps.items" :key="index" class="col-3 p-2">
          <ConfirmationCard
            :confirmation="item"
            @getActionConfirmationRequests="getConfirmationRequiringActions()"
          ></ConfirmationCard>
        </div>
      </div>
    </template>
  </DataView>
</template>

<script setup>
import { ref, onBeforeMount, onUnmounted } from 'vue'
import { useActionConfirmationComposable } from '@/composables/ActionConfirmationComposable.js'
import ConfirmationCard from '@/components/action_confirmation/ConfirmationCard.vue'
import { useUserComposable } from '@/composables/UserComposable.js'

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)
onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      getConfirmationRequiringActions()
      statusCaller = setInterval(() => {
        getConfirmationRequiringActions()
      }, 1500)
      userLoggedEmbedded.value = true
    })
  } else {
    getConfirmationRequiringActions()
    userLoggedEmbedded.value = true
    statusCaller = setInterval(() => {
      getConfirmationRequiringActions()
    }, 1500)
  }
})
const { getConfirmationRequiringActions, confirmationRequests } = useActionConfirmationComposable()

var statusCaller = null

onUnmounted(() => {
  clearInterval(statusCaller)
})

const layout = ref('grid')
</script>
