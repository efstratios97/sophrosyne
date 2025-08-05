<template>
  <div>
    <Card class="sophrosyne-card">
      <template #header>
        <div class="sophrosyne-center-element">
          <img
            class="border-round w-full"
            src="@/assets/images/ACTION_RECOMMENDATION.gif"
            alt=""
            style="max-width: 300px; max-height: 200px"
          />
        </div>
      </template>
      <template #title>{{ props.actionRecommendation.name }}</template>
      <template #subtitle>
        <div class="grid">
          <div class="col-3">
            {{ $t('action_recommendations.action_recommendation_card.fields.responsible_entity') }}
          </div>
          <div class="col-9">
            {{ props.actionRecommendation.responsibleEntity }}
          </div>
          <div class="col-3">
            {{ $t('action_recommendations.action_recommendation_card.fields.contact_information') }}
          </div>
          <div class="col-9">
            {{ props.actionRecommendation.contactInformation }}
          </div>
        </div>
      </template>
      <template #content>
        <div class="grid">
          <div class="col-4">
            {{ $t('action_recommendations.action_recommendation_card.fields.description') }}
          </div>
          <div class="col-8">
            <DisplayActionRecommendationDescription
              :description="actionRecommendation.description"
            ></DisplayActionRecommendationDescription>
          </div>
          <div class="col-4">
            {{
              $t('action_recommendations.action_recommendation_card.fields.additional_description')
            }}
          </div>
          <div class="col-8">
            <DownloadActionRecommendationAdditionalDocumentation
              :actionRecommendation="actionRecommendation"
            ></DownloadActionRecommendationAdditionalDocumentation>
          </div>
        </div>
      </template>
      <template #footer>
        <div class="flex gap-3 mt-1">
          <Button
            :label="$t('action_recommendations.action_recommendation_card.fields.acknowledgement')"
            class="w-full"
            @click="acknowledgeActionRecommendation()"
          />
        </div>
      </template>
    </Card>
    <ConfirmDialog :group="actionRecommendation.id">
      <template #message="slotProps">
        <div
          class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
        >
          <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
          <p>{{ slotProps.message.message }}</p>
        </div>
      </template>
    </ConfirmDialog>
  </div>
</template>
<script setup>
import { ref, defineProps, inject } from 'vue'
import { useConfirm } from 'primevue/useconfirm'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import DisplayActionRecommendationDescription from '@/components/action_recommendation/utils/DisplayActionRecommendationDescription.vue'
import DownloadActionRecommendationAdditionalDocumentation from '@/components/action_recommendation/utils/DownloadActionRecommendationAdditionalDocumentation.vue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const props = defineProps(['actionRecommendation'])
const emit = defineEmits(['getActiveActionRecommendations'])

const confirm = useConfirm()
const acknowledgeActionRecommendation = () => {
  var actionRecommendation = ref({ ...props.actionRecommendation })
  confirm.require({
    group: actionRecommendation.value.id,
    header: t('action_recommendations.action_recommendation_card.confirmation_dialog.title'),
    message: t(
      'action_recommendations.action_recommendation_card.confirmation_dialog.confirmation.message'
    ),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      axiosCore
        .post('/int/client/actionrecommendation/' + actionRecommendation.value.id + '/deactivate')
        .then(() => {
          toast.add({
            severity: 'success',
            summary: t(
              'action_recommendations.action_recommendation_card.confirmation_dialog.confirmation.toast.success.message'
            ),
            detail: t(
              'action_recommendations.action_recommendation_card.confirmation_dialog.confirmation.toast.success.detail'
            ),
            life: 3000
          })
          emit('getActiveActionRecommendations')
        })
        .catch(() => {
          toast.add({
            severity: 'error',
            summary: t(
              'action_recommendations.action_recommendation_card.confirmation_dialog.confirmation.toast.error.message'
            ),
            detail: t(
              'action_recommendations.action_recommendation_card.confirmation_dialog.confirmation.toast.error.detail'
            ),
            life: 3000
          })
        })
    },
    reject: () => {}
  })
}
</script>
