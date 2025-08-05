import { ref, inject } from 'vue'

export const useDynamicActionComposable = () => {
  const axiosCore = inject('axios-core')

  const dynamicActions = ref([])

  const getDynamicActions = () => {
    axiosCore.get('/int/user/dynamicactions').then((res) => {
      dynamicActions.value = res.data
    })
  }

  const runningDynamicActions = ref([])

  const getRunningDynamicActions = async () => {
    await axiosCore.get('/int/client/executor/dynamicactions').then((res) => {
      runningDynamicActions.value = res.data
    })
  }

  const runningDynamicActionsById = ref([])

  const getRunningDynamicActionsById = async (id) => {
    await axiosCore.get('/int/client/executor/running/dynamicaction/' + id).then((res) => {
      runningDynamicActionsById.value = res.data
    })
  }

  return {
    getDynamicActions,
    dynamicActions,
    getRunningDynamicActions,
    runningDynamicActions,
    getRunningDynamicActionsById,
    runningDynamicActionsById
  }
}
