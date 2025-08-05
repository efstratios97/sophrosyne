import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useActionRecommendationComposable = () => {
  const { t } = useI18n()
  const toast = useToast()

  const axiosCore = inject('axios-core')
  const actionRecommendationStatusInfo = ref({})

  const getActionRecommendationStatusInfo = async (id) => {
    await axiosCore.get('/pull/user/actionrecommendation/' + id + '/status').then((res) => {
      if (res.data.isActive) {
        actionRecommendationStatusInfo.value.isActive = 'success'
        actionRecommendationStatusInfo.value.isActiveText = 'active'
      } else {
        actionRecommendationStatusInfo.value.isActive = 'info'
        actionRecommendationStatusInfo.value.isActiveText = 'inactive'
      }
    })
  }

  const downloadAdditionalDocumentation = (actionRecommendation) => {
    try {
      toast.add({
        severity: 'info',
        summary: t(
          'action_recommendations.action_recommendation_additional_documentation.download.toast.info.message'
        ),
        detail: t(
          'action_recommendations.action_recommendation_additional_documentation.download.toast.info.detail'
        ),
        life: 1000
      })
      // Decode Base64 string to binary data
      let binaryString = atob(actionRecommendation.additionalDocumentation)

      // Convert binary string to Uint8Array
      let byteArray = new Uint8Array(binaryString.length)
      for (let i = 0; i < binaryString.length; i++) {
        byteArray[i] = binaryString.charCodeAt(i)
      }

      // Create Blob from Uint8Array
      let blob = new Blob([byteArray], { type: 'application/pdf' })

      // Create URL for the Blob
      let url = URL.createObjectURL(blob)

      // Create link element
      let link = document.createElement('a')
      link.href = url
      link.download = actionRecommendation.name // Set download attribute to filename

      // Simulate click on the link to trigger download
      link.click()

      // Clean up by revoking the URL object
      URL.revokeObjectURL(url)
      toast.add({
        severity: 'success',
        summary: t(
          'action_recommendations.action_recommendation_additional_documentation.download.toast.success.message'
        ),
        detail: t(
          'action_recommendations.action_recommendation_additional_documentation.download.toast.success.detail'
        ),
        life: 3000
      })
    } catch (err) {
      toast.add({
        severity: 'error',
        summary: t(
          'action_recommendations.action_recommendation_additional_documentation.download.error.success.message'
        ),
        detail: t(
          'action_recommendations.action_recommendation_additional_documentation.download.error.success.detail'
        ),
        life: 3000
      })
    }
  }

  return {
    downloadAdditionalDocumentation,
    getActionRecommendationStatusInfo,
    actionRecommendationStatusInfo
  }
}
