import { createApp } from 'vue'
// Pinia
import { createPinia } from 'pinia'
// Vue router
import router from './router'
// Vue I18n
import i18n from './language_I18n/i18n'
// axios
import axios from 'axios'
// Prime Vue
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'

import 'primeflex/primeflex.css'
import 'primeflex/primeflex.scss'
import 'primeicons/primeicons.css'
import '@fontsource/inter/400.css'
import '@fontsource/inter/500.css'
import '@fontsource/inter/600.css'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Menu from 'primevue/menu'
import Menubar from 'primevue/menubar'
import Avatar from 'primevue/avatar'
import Carousel from 'primevue/carousel'
import Card from 'primevue/card'
import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import TabPanels from 'primevue/tabpanels';
import TabPanel from 'primevue/tabpanel';
import InputSwitch from 'primevue/inputswitch'
import Toast from 'primevue/toast'
import ToastService from 'primevue/toastservice'
import ProgressBar from 'primevue/progressbar'
import Tooltip from 'primevue/tooltip'
import Panel from 'primevue/panel'
import Checkbox from 'primevue/checkbox'
import Toolbar from 'primevue/toolbar'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Dialog from 'primevue/dialog'
import ConfirmDialog from 'primevue/confirmdialog'
import ConfirmationService from 'primevue/confirmationservice'
import Dropdown from 'primevue/dropdown'
import OverlayPanel from 'primevue/overlaypanel'
import Terminal from 'primevue/terminal'
import MultiSelect from 'primevue/multiselect'
import Badge from 'primevue/badge'
import ScrollPanel from 'primevue/scrollpanel'
import Chart from 'primevue/chart'
import DataView from 'primevue/dataview'
import ToggleButton from 'primevue/togglebutton'
import FileUpload from 'primevue/fileupload'
import Textarea from 'primevue/textarea'
import Splitter from 'primevue/splitter'
import SplitterPanel from 'primevue/splitterpanel'
import Fieldset from 'primevue/fieldset'
import InputGroup from 'primevue/inputgroup'
import InputGroupAddon from 'primevue/inputgroupaddon'
import InputNumber from 'primevue/inputnumber'
import FloatLabel from 'primevue/floatlabel'
import SelectButton from 'primevue/selectbutton'
import App from './App.vue'

const app = createApp(App)

app.component('Button', Button)
app.component('InputText', InputText)
app.component('Menu', Menu)
app.component('Menubar', Menubar)
app.component('Avatar', Avatar)
app.component('Carousel', Carousel)
app.component('Card', Card)
app.component('TabPanel', TabPanel)
app.component('TabPanels', TabPanels)
app.component('TabList', TabList)
app.component('Tab', Tab)
app.component('Tabs', Tabs)
app.component('InputSwitch', InputSwitch)
app.component('Toast', Toast)
app.component('ProgressBar', ProgressBar)
app.component('Panel', Panel)
app.component('Checkbox', Checkbox)
app.component('Toolbar', Toolbar)
app.component('DataTable', DataTable)
app.component('Column', Column)
app.component('ConfirmDialog', ConfirmDialog)
app.component('Dialog', Dialog)
app.component('Dropdown', Dropdown)
app.component('OverlayPanel', OverlayPanel)
app.component('Terminal', Terminal)
app.component('MultiSelect', MultiSelect)
app.component('Badge', Badge)
app.component('ScrollPanel', ScrollPanel)
app.component('Chart', Chart)
app.component('DataView', DataView)
app.component('ToggleButton', ToggleButton)
app.component('FileUpload', FileUpload)
app.component('Textarea', Textarea)
app.component('Splitter', Splitter)
app.component('SplitterPanel', SplitterPanel)
app.component('Fieldset', Fieldset)
app.component('InputGroup', InputGroup)
app.component('InputGroupAddon', InputGroupAddon)
app.component('InputNumber', InputNumber)
app.component('FloatLabel', FloatLabel)
app.component('SelectButton', SelectButton)

app.directive('tooltip', Tooltip)

app.use(createPinia())
app.use(ConfirmationService)
app.use(ToastService)

app.use(PrimeVue, {
  theme: {
    preset: Aura,
    ripple: true,
    // zIndex: {
    //   modal: 1100, //dialog, drawer
    //   overlay: 1000, //select, popover
    //   menu: 1000, //overlay menus
    //   tooltip: 1100 //tooltip
    // },
    options: {
      prefix: 'p',
      darkModeSelector: 'light',
      cssLayer: false
    }
  }
})
app.use(router)
app.use(i18n)

const api = axios.create({
  baseURL: `${window.location.protocol}//${window.location.hostname}:17609`,
  timeout: 60 * 15 * 100000000,
  withCredentials: true,
  crossdomain: true
})

import { useLoggedUser } from './stores/loggedUser'
api.interceptors.request.use(
  function (config) {
    const userStore = useLoggedUser()
    config.headers['Authorization'] = userStore.loggedUserToken
    config.headers['X-Username'] = userStore.loggedUserUsername
    config.headers['Content-Type'] = 'application/json'
    return config
  },
  function (error) {
    return Promise.reject(error)
  }
)
api.interceptors.response.use(
  function (response) {
    return response
  },
  function (error) {
    if (error.response.status) {
      switch (error.response.status) {
        case 401:
          router.push('/')
          break
        case 403:
          router.push('/')
          break
        case 500:
          return Promise.reject(error)
        default:
          return Promise.reject(error)
      }
    }
  }
)

const instanceCore = api
app.provide('axios-core', instanceCore)

app.mount('#app')
