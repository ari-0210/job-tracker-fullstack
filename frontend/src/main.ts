import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
import router from "./router";
import naive from "naive-ui";

import { createPinia } from "pinia";
const pinia = createPinia();

createApp(App)
  .use(router)
  .use(naive as any)
  .use(pinia)
  .mount("#app");
