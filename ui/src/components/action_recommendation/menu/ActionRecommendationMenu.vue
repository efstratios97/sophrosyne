<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="New"
          icon="pi pi-plus"
          severity="success"
          class="mr-2"
          @click="toggleCreateActionRecommendation"
        />
        <Button
          label="Update"
          icon="pi pi-replay"
          severity="primary"
          class="mr-2"
          @click="toggleUpdateActionRecommendation"
          :disabled="
            selectedActionRecommendations.length == 0 || selectedActionRecommendations.length > 1
          "
        />
        <Button
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteActionRecommendations"
          :disabled="!selectedActionRecommendations || !selectedActionRecommendations.length"
        />
      </template>

      <template #end> </template>
    </Toolbar>
    <div class="sophrosyne-card">
      <DataTable
        v-model:selection="selectedActionRecommendations"
        :value="actionRecommendations"
        dataKey="name"
        tableStyle="min-width: 60rem"
        scrollable
        scrollHeight="100vh"
      >
        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column
          field="id"
          :header="
            $t('action_recommendations.action_recommendation_menu.datatable.column_header.id')
          "
        ></Column>
        <Column
          field="name"
          :header="
            $t('action_recommendations.action_recommendation_menu.datatable.column_header.name')
          "
        ></Column>

        <Column
          field="description"
          :header="
            $t(
              'action_recommendations.action_recommendation_menu.datatable.column_header.description'
            )
          "
        >
          <template #body="slotProps">
            <DisplayActionRecommendationDescription
              :description="slotProps.data.description"
            ></DisplayActionRecommendationDescription>
          </template>
        </Column>
        <Column
          field="additionalDocumentation"
          :header="
            $t(
              'action_recommendations.action_recommendation_menu.datatable.column_header.additional_documentation'
            )
          "
        >
          <template #body="slotProps">
            <DownloadActionRecommendationAdditionalDocumentation
              :actionRecommendation="slotProps.data"
            ></DownloadActionRecommendationAdditionalDocumentation>
          </template>
        </Column>
        <Column
          field="responsibleEntity"
          :header="
            $t(
              'action_recommendations.action_recommendation_menu.datatable.column_header.responsible_entity'
            )
          "
        >
        </Column>
        <Column
          field="contactInformation"
          :header="
            $t(
              'action_recommendations.action_recommendation_menu.datatable.column_header.contact_information'
            )
          "
        >
        </Column>
        <Column
          field="allowedApikeys"
          :header="
            $t(
              'action_recommendations.action_recommendation_menu.datatable.column_header.allowed_apikeys'
            )
          "
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.allowedApikeys"
              :placeholder="
                $t(
                  'action_recommendations.action_recommendation_menu.datatable.column_header.allowed_apikeys'
                )
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
      </DataTable>
      <ConfirmDialog group="deleteActionRecommendations">
        <template #message="slotProps">
          <div
            class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
          >
            <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
            <p>{{ slotProps.message.message }}</p>
            <ul v-for="action in selectedActionRecommendations" :key="action" class="list-disc">
              <li>{{ action.name }}</li>
            </ul>
          </div>
        </template>
      </ConfirmDialog>
      <Dialog
        v-model:visible="showCreateActionRecommendation"
        modal
        maximizable
        :header="$t('action_recommendations.action_recommendation_creation_form.modal_header')"
        v-if="showCreateActionRecommendation"
        @close="toggleCreateActionRecommendation"
      >
        <ActionRecommendationFormCreate
          @getActionRecommendations="getActionRecommendations"
          @close="toggleCreateActionRecommendation"
        ></ActionRecommendationFormCreate>
      </Dialog>
      <Dialog
        v-model:visible="showUpdateActionRecommendation"
        modal
        maximizable
        :header="
          $t('action_recommendations.action_recommendation_creation_form.modal_header_update')
        "
        v-if="showUpdateActionRecommendation"
        @close="toggleUpdateActionRecommendation"
      >
        <ActionRecommendationFormUpdate
          :actionRecommendation="selectedActionRecommendations[0]"
          @getActionRecommendations="getActionRecommendations"
          @close="toggleUpdateActionRecommendation"
        ></ActionRecommendationFormUpdate>
      </Dialog>
      <Toast />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import ActionRecommendationFormCreate from '@/components/action_recommendation/menu/ActionRecommendationFormCreate.vue'
import ActionRecommendationFormUpdate from '@/components/action_recommendation/menu/ActionRecommendationFormUpdate.vue'
import DisplayActionRecommendationDescription from '@/components/action_recommendation/utils/DisplayActionRecommendationDescription.vue'
import DownloadActionRecommendationAdditionalDocumentation from '@/components/action_recommendation/utils/DownloadActionRecommendationAdditionalDocumentation.vue'
import ActionRecommendationActiveStatus from '@/components/action_recommendation/menu/ActionRecommendationActiveStatus.vue'
import { Dialog } from 'primevue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const actionRecommendations = ref([])
const selectedActionRecommendations = ref([])

onMounted(() => {
  getActionRecommendations()
})

const showCreateActionRecommendation = ref(false)
const toggleCreateActionRecommendation = () => {
  showCreateActionRecommendation.value = !showCreateActionRecommendation.value
}

const showUpdateActionRecommendation = ref(false)
const toggleUpdateActionRecommendation = () => {
  showUpdateActionRecommendation.value = !showUpdateActionRecommendation.value
}

const getActionRecommendations = () => {
  axiosCore.get('/int/user/actionrecommendations').then((res) => {
    actionRecommendations.value = res.data
  })
  showCreateActionRecommendation.value = false
  showUpdateActionRecommendation.value = false
}

const confirm = useConfirm()
const deleteActionRecommendations = () => {
  confirm.require({
    group: 'deleteActionRecommendations',
    header: t(
      'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.title'
    ),
    message: t(
      'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.confirmation.message'
    ),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      selectedActionRecommendations.value.forEach((actionRecommendation) => {
        axiosCore
          .delete('/int/user/actionrecommendation/' + actionRecommendation.id)
          .then(() => {
            toast.add({
              severity: 'success',
              summary: t(
                'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.confirmation.toast.success.message'
              ),
              detail: t(
                'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.confirmation.toast.success.detail'
              ),
              life: 3000
            })
            selectedActionRecommendations.value = []
            getActionRecommendations()
          })
          .catch(() => {
            toast.add({
              severity: 'error',
              summary: t(
                'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.confirmation.toast.error.message'
              ),
              detail: t(
                'action_recommendations.action_recommendation_menu.deletion_action_confirmation.confirmation_dialog.confirmation.toast.error.detail'
              ),
              life: 3000
            })
            selectedActionRecommendations.value = []
          })
      })
    },
    reject: () => {}
  })
}
</script>
