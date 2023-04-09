// import Vue from 'vue'
// import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import AxiosTest from '@/components/AxiosTest'

const Foo = { template: '<div>foo</div>' }
const Bar = { template: '<div>bar</div>' }

// Vue.use(Router)

// export default new Router({
//   routes: [
//     {
//       path: '/',
//       name: 'HelloWorld',
//       component: HelloWorld
//     },
//     {
//       path: '/foo',
//       component: Foo
//     },
//     {
//       path: '/bar',
//       component: Bar
//     },
//     {
//       path: '/AxiosTest',
//       component: AxiosTest
//     }
//   ]
// })
export default [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/foo',
      name: 'foo',
      component: Foo
    },
    {
      path: '/bar',
      name: 'bar',
      component: Bar
    },
    {
      path: '/AxiosTest',
      name: 'AxiosTest',
      component: AxiosTest
    }
]