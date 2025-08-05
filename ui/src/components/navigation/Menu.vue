<template>
  <Menu
    ref="menu"
    id="overlay_menu"
    :model="items"
    class="border-round"
    style="height: 100%; overflow: auto"
    :popup="menuToggle"
  >
    <template #start>
      <img src="@/assets/images/logos/Sophrosyne_small.png" alt="Image" style="width: 50%" />
    </template>
    <template #submenuheader="{ item }">
      <span class="text-primary font-bold">{{ item.label }}</span>
    </template>
    <template #item="{ item, props }">
      <a v-ripple class="flex align-items-center" v-bind="props.action" @click="navigate">
        <span :class="item.icon" />
        <span class="ml-2">{{ item.label }}</span>
      </a>
    </template>
    <template #end>
      <Button class="w-full" severity="secondary" @mouseenter="toggleSubmenu">
        <Avatar
          :label="userStore.loggedUserRole == 'ADMIN' ? 'A' : 'U'"
          class="mr-2"
          size="medium"
          style="background-color: #2196f3; color: #ffffff"
          shape="circle"
        />
        <span class="inline-flex flex-column">
          <span class="font-bold">{{ userStore.loggedUserUsername }}</span>
          <span class="text-sm">{{ userStore.loggedUserRole.toLocaleLowerCase() }}</span>
        </span>
      </Button>
      <OverlayPanel ref="submenu"
        ><Menu :model="items_submenu" class="border-round"></Menu>
      </OverlayPanel>
      <Toast />
    </template>
  </Menu>
  <Button
    @click="toggleMenu"
    class="w-full"
    severity="secondary"
    variant-text
    style="height: 2vh"
    icon="pi pi-bars"
    raised
  >
  </Button>
</template>
<script setup>
import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useLoggedUser } from '@/stores/loggedUser'
import router from '@/router'

const submenu = ref()
const toggleSubmenu = (event) => {
  submenu.value.toggle(event)
}

const menuToggle = ref(false)
const toggleMenu = () => {
  menuToggle.value = !menuToggle.value
}

const axiosCore = inject('axios-core')
const userStore = useLoggedUser()
const toast = useToast()
const { t } = useI18n()

const items_submenu = ref([
  {
    label: 'User Menu',
    items: [
      {
        label: 'Edit Profile',
        icon: 'pi pi-user-edit',
        command: () => {
          router.push('/profile')
        }
      },
      {
        label: 'Logout',
        icon: 'pi pi-power-off',
        command: () => {
          axiosCore
            .post('/int/client/' + userStore.loggedUserUsername + '/logout')
            .then(() => {
              toast.add({
                severity: 'success',
                summary: t('logout.toast.success.message'),
                detail: t('logout.toast.success.detail'),
                life: 3000
              })
            })
            .catch(() => {
              toast.add({
                severity: 'error',
                summary: t('logout.toast.error.message'),
                detail: t('logout.toast.error.detail'),
                life: 3000
              })
            })
          router.push('/')
        }
      }
    ]
  }
])
const items_client = ref([
  {
    separator: true
  },
  {
    label: 'Action Menu',
    items: [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        command: () => {
          router.push('/home')
        }
      },
      {
        label: 'Control Panel',
        icon: 'pi pi-globe',
        command: () => {
          router.push('/control-panel-view')
        }
      },
      {
        label: 'Action Confirmation',
        icon: 'pi pi-check-square',
        command: () => {
          router.push('/action-confirmation')
        }
      },
      {
        label: 'Action Recommendation',
        icon: 'pi pi-file-check',
        command: () => {
          router.push('/action-recommendation')
        }
      },
      {
        label: 'Archive',
        icon: 'pi pi-book',
        command: () => {
          router.push('/actions-archive')
        }
      }
    ]
  },
  {
    separator: true
  }
])
const items_user = ref([
  {
    separator: true
  },
  {
    label: 'Action Menu',
    items: [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        command: () => {
          router.push('/home')
        }
      },
      {
        label: 'Control Panel',
        icon: 'pi pi-globe',
        command: () => {
          router.push('/control-panel-view')
        }
      },
      {
        label: 'Action Confirmation',
        icon: 'pi pi-check-square',
        command: () => {
          router.push('/action-confirmation')
        }
      },
      {
        label: 'Action Recommendation',
        icon: 'pi pi-file-check',
        command: () => {
          router.push('/action-recommendation')
        }
      },
      {
        label: 'Recommendations Management',
        icon: 'pi pi-compass',
        command: () => {
          router.push('/action-recommendation-menu')
        }
      },
      {
        label: 'Control Panel Management',
        icon: 'pi pi-sliders-h',
        command: () => {
          router.push('/control-panel-menu')
        }
      },
      {
        label: 'Actions Management',
        icon: 'pi pi-megaphone',
        command: () => {
          router.push('/actions')
        }
      },
      {
        label: 'Archive',
        icon: 'pi pi-book',
        command: () => {
          router.push('/actions-archive')
        }
      }
    ]
  },
  {
    separator: true
  }
])
const items_admin = ref([
  {
    separator: true
  },
  {
    label: 'Action Menu',
    items: [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        command: () => {
          router.push('/home')
        }
      },
      {
        label: 'Control Panel',
        icon: 'pi pi-globe',
        command: () => {
          router.push('/control-panel-view')
        }
      },
      {
        label: 'Action Confirmation',
        icon: 'pi pi-check-square',
        command: () => {
          router.push('/action-confirmation')
        }
      },
      {
        label: 'Action Recommendation',
        icon: 'pi pi-file-check',
        command: () => {
          router.push('/action-recommendation')
        }
      },
      {
        label: 'Recommendations Management',
        icon: 'pi pi-compass',
        command: () => {
          router.push('/action-recommendation-menu')
        }
      },
      {
        label: 'Control Panel Management',
        icon: 'pi pi-sliders-h',
        command: () => {
          router.push('/control-panel-menu')
        }
      },
      {
        label: 'Actions Management',
        icon: 'pi pi-megaphone',
        command: () => {
          router.push('/actions')
        }
      },
      {
        label: 'Archive',
        icon: 'pi pi-book',
        command: () => {
          router.push('/actions-archive')
        }
      }
    ]
  },
  {
    label: 'Admin Area',
    items: [
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        command: () => {
          router.push('/settings')
        }
      }
    ]
  },
  {
    separator: true
  }
])

const items = ref(
  userStore.loggedUserRole == 'ADMIN'
    ? items_admin
    : userStore.loggedUserRole == 'USER'
      ? items_user
      : items_client
)
</script>

<style></style>
