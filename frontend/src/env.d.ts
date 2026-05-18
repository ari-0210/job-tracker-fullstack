import {
  MessageApiInjection,
  DialogApiInjection,
  NotificationApiInjection,
} from "naive-ui";
declare global {
  interface Window {
    $message?: MessageApiInjection;
    $dialog: DialogApiInjection;
    $notification: NotificationApiInjection;
  }
}
export {};
