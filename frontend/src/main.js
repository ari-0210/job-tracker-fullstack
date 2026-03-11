import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import naive from "naive-ui";
import "./assets/styles.css";
import { createPinia } from "pinia";
const pinia = createPinia();

createApp(App).use(router).use(naive).use(pinia).mount("#app");
