export default [
  {
    path: '/user',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { role: 'USER' },
    children: [
      { path: 'profile', component: () => import('@/views/user/Profile.vue') },
      { path: 'orders', component: () => import('@/views/user/Orders.vue') }
    ]
  }
]
