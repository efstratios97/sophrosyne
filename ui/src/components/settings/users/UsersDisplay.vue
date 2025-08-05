<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="New"
          icon="pi pi-plus"
          severity="success"
          class="mr-2"
          @click="toggleAddUser"
        />
        <Button
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteUsers"
          :disabled="!selectedUsers || !selectedUsers.length"
        />
      </template>

      <template #end> </template>
    </Toolbar>
    <div class="sophrosyne-card">
      <DataTable
        :value="users"
        v-model:selection="selectedUsers"
        :paginator="true"
        :rows="10"
        columnResizeMode="fit"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        :rowsPerPageOptions="[5, 10, 25]"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords} users"
      >
        <template #header>
          <h4 class="m-0">{{ t('settings.users.users_display.title') }}</h4>
        </template>

        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column field="username" header="Username" sortable></Column>
        <Column field="firstName" header="First Name" sortable></Column>
        <Column field="lastName" header="Last Name" sortable></Column>
        <Column field="email" header="Email" sortable></Column>
        <Column field="role" header="Role" sortable></Column>
      </DataTable>
      <ConfirmDialog group="deleteUsers">
        <template #message="slotProps">
          <div
            class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
          >
            <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
            <p>{{ slotProps.message.message }}</p>
            <ul v-for="user in selectedUsers" :key="user" class="list-disc">
              <li>{{ user.username }}</li>
            </ul>
          </div>
        </template>
      </ConfirmDialog>
      <Dialog
        v-model:visible="showAddUser"
        modal
        maximizable
        :header="$t('settings.users.user_add_modal.title')"
        v-if="showAddUser"
        @close="toggleAddUser"
      >
        <UsersAddUser @close="toggleAddUser" @getUsers="getUsers()"></UsersAddUser>
      </Dialog>
      <Toast />
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import UsersAddUser from '@/components/settings/users/UsersAddUser.vue'
import { useUserComposable } from '@/composables/UserComposable.js'
import { Dialog } from 'primevue'

const { getUsers, users } = useUserComposable()

onBeforeMount(() => {
  getUsers().then(() => {
    selectedUsers.value = []
  })
})

const axiosCore = inject('axios-core')
const toast = useToast()
const { t } = useI18n()

const selectedUsers = ref([])

const showAddUser = ref(false)
const toggleAddUser = () => {
  showAddUser.value = !showAddUser.value
}

const confirm = useConfirm()
const deleteUsers = () => {
  confirm.require({
    group: 'deleteUsers',
    header: t('settings.users.users_delete_dialog.title'),
    message: t('settings.users.users_delete_dialog.message'),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      selectedUsers.value.forEach((user) => {
        axiosCore
          .delete('/int/admin/user/' + user.username)
          .then(() => {
            toast.add({
              severity: 'success',
              summary: t('settings.users.users_delete_dialog.delete_toast.success.message'),
              detail: t('settings.users.users_delete_dialog.delete_toast.success.detail'),
              life: 3000
            })
            selectedUsers.value = []
            getUsers().then(() => {
              selectedUsers.value = []
            })
          })
          .catch((error) => {
            if (error.response.status == 406) {
              toast.add({
                severity: 'error',
                summary: t('settings.users.users_delete_dialog.delete_toast_406.error.message'),
                detail: t('settings.users.users_delete_dialog.delete_toast_406.error.detail'),
                life: 5000
              })
            } else {
              toast.add({
                severity: 'error',
                summary: t('settings.users.users_delete_dialog.delete_toast.error.message'),
                detail: t('settings.users.users_delete_dialog.delete_toast.error.detail'),
                life: 3000
              })
            }
            selectedUsers.value = []
          })
      })
    },
    reject: () => {}
  })
}
</script>
