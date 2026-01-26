export default [
  {
    path: '/',
    component: () => import('@/layouts/GuestLayout.vue'),
    children: [
      { path: '', component: () => import('@/views/guest/Home.vue') },
      { path: 'login', component: () => import('@/views/auth/Login.vue') },
      { path: 'register', component: () => import('@/views/auth/Register.vue') }
    ]
  }
]
