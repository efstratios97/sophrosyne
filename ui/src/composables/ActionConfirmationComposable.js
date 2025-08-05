import { ref, inject } from 'vue'

export const useActionConfirmationComposable = () => {
  const axiosCore = inject('axios-core')
  const confirmationRequests = ref([])

  const getConfirmationRequiringActions = async () => {
    await axiosCore.get('/int/client/confirmations').then((res) => {
      confirmationRequests.value = res.data
    })
  }
  return {
    getConfirmationRequiringActions,
    confirmationRequests
  }
}
