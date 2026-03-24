import { createApp } from "vue";
import { use } from "echarts/core";
import "./style.css";
import App from "./App.vue";
import router from "./router";
import naive from "naive-ui";
import ECharts from "vue-echarts";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
} from "echarts/components";

import { createPinia } from "pinia";
const pinia = createPinia();
use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
]);
createApp(App)
  .use(router)
  .use(naive as any)
  .use(pinia)
  .component("v-chart", ECharts)
  .mount("#app");
