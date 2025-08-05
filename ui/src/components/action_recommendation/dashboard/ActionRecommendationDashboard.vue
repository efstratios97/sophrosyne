<template>
  <DataView v-if="userLoggedEmbedded" :value="activeActionRecommendations" :layout="layout">
    <template #header>
      <div class="flex justify-content-end">
        <DataViewLayoutOptions v-model="layout" />
      </div>
    </template>

    <template #list="slotProps">
      <div class="grid grid-nogutter w-full">
        <div v-for="(item, index) in slotProps.items" :key="index" class="col-12">
          <ActionRecommendationCard
            :actionRecommendation="item"
            @getActionConfirmationRequests="getActiveActionRecommendations()"
          ></ActionRecommendationCard>
          <br />
        </div>
      </div>
    </template>

    <template #grid="slotProps">
      <div class="grid grid-nogutter w-full">
        <div v-for="(item, index) in slotProps.items" :key="index" class="col-3 p-2">
          <ActionRecommendationCard
            :actionRecommendation="item"
            @getActiveActionRecommendations="getActiveActionRecommendations()"
          ></ActionRecommendationCard>
        </div>
      </div>
    </template>
  </DataView>
</template>

<script setup>
import { ref, inject, onBeforeMount, onUnmounted } from 'vue'
import ActionRecommendationCard from '@/components/action_recommendation/dashboard/ActionRecommendationCard.vue'
import { useUserComposable } from '@/composables/UserComposable.js'

const axiosCore = inject('axios-core')

const layout = ref('grid')

const activeActionRecommendations = ref([])
var statusCaller = null

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)

onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      userLoggedEmbedded.value = true
      getActiveActionRecommendations()
      statusCaller = setInterval(() => {
        getActiveActionRecommendations()
      }, 1500)
    })
  } else {
    getActiveActionRecommendations()
    statusCaller = setInterval(() => {
      getActiveActionRecommendations()
    }, 1500)
    userLoggedEmbedded.value = true
  }
})

onUnmounted(() => {
  clearInterval(statusCaller)
})

const getActiveActionRecommendations = () => {
  axiosCore.get('/int/client/actionrecommendations/active').then((res) => {
    activeActionRecommendations.value = res.data
  })
}
</script>
