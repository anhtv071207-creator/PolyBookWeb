import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Home from "../views/Homepage.vue";
import BookDetail from "../views/BookDetail.vue";
import Login from "../views/Login.vue";
import Cart from "../views/Cart.vue";
import Order from "../views/Order.vue";
import OrderManagement from "../views/OrderManagement.vue";
import OrderDetail from "../views/OrderDetail.vue";
import AccountManagement from "../views/AccountManagement.vue";
import ViewOrder from "../views/ViewOrder.vue";
import ViewOrderDetail from "../views/ViewOrderDetail.vue";
import AccountManagementDetail from "../views/AccountManagementDetail.vue";
import RemoteManagement from "../views/RemoteManagement.vue";
import Register from "../views/Register.vue";
import Infomation from "../views/Infomation.vue";

const routes = [
  { path: "/", component: Home },
  { path: "/login", component: Login },
  { path: "/book/:id", component: BookDetail },
  { path: "/cart", component: Cart },
  { path: "/order", component: Order },
  { path: "/management/orders", component: OrderManagement },
  { path: "/management/orders/:id", component: OrderDetail, props: true },
  { path: "/managements/account", component: AccountManagement },
  { path: "/admin/users/:id", component: AccountManagementDetail },
  { path: "/orders/view", component: ViewOrder },
  { path: "/orders/view/:id", component: ViewOrderDetail },
  { path: "/managements", component: RemoteManagement },
  { path: "/register", component: Register },
  { path: "/info", component: Infomation },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(() => {
  const auth = useAuthStore();
  if (!auth.isLoggedIn && localStorage.getItem("token")) {
    auth.initAuth();
  }
});

export default router;
