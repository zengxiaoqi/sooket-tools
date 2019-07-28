import Vue from 'vue'
import Router from 'vue-router'


import Layout from '@/views/layout'

Vue.use(Router)
/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */
export const constantRoutes =[
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: 'Dashboard', icon: 'dashboard' }
    }]
  },
  {
    path: '/tcp',
    component: Layout,
    redirect: '/tcp/tcp-server',
    name: 'TCP',
    meta: { title: 'TCP', icon: 'net' },
    children: [
      {
        path: 'tcp-server',
        name: 'tcp-server',
        component: () => import('@/views/tcp/tcp-server'),
        meta: { title: 'tcp-server', icon: 'server' }
      },
      {
        path: 'tcp-client',
        name: 'tcp-client',
        component: () => import('@/views/tcp/tcp-client'),
        meta: { title: 'tcp-client', icon: 'server' }
      },
    ]
  },
    {
        path: '/http',
        component: Layout,
        redirect: 'noRedirect',
        name: 'HTTP',
        meta: { title: 'HTTP', icon: 'net' },
        children: [
            {
                path: 'http-server',
                name: 'http-server',
                component: () => import('@/views/http/http-server'),
                meta: { title: 'http-server', icon: 'server' }
            },
            {
                path: 'http-client',
                name: 'http-client',
                component: () => import('@/views/http/http-client'),
                meta: { title: 'http-client', icon: 'server' }
            },
        ]
    },
  {
    path: '/format',
    component: Layout,
    redirect: '/format/string-format',
    children: [
      {
        path: 'string-format',
        name: 'format',
        component: () => import('@/views/format/string-format'),
        meta: { title: 'StrFormat', icon: 'format-text' }
      }
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
]



export default new Router({
  routes: constantRoutes
})
