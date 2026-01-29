import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Homepage.vue";
import BookDetail from "../views/BookDetail.vue";
import Login from "../views/Login.vue";
import Cart from '../views/Cart.vue'
import Order from "../views/Order.vue";
import OrderManagement from "../views/OrderManagement.vue";
import OrderDetail from "../views/OrderDetail.vue";
const routes = [
  {
    path: "/",
    name: "home",
    component: Home,
  },
  {
    path: "/login",
    name: "login",
    component: Login,
  },
  {
    path: "/book/:id",
    name: "book-detail",
    component: BookDetail,
  },
  {
    path: "/cart",
    name: "cart",
    component: Cart,
  },
  {
    path: "/order",
    name: "order",
    component: Order,
  },
  {
    path: "/management/orders",
    name: "ordermanagement",
    component: OrderManagement,
  },
   {
    path: "/management/orders/:id",
    component: OrderDetail,
    props: true,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
