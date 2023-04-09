import Vue from 'vue'
import Router from 'vue-router'
import indexRouter from './index'
import AvueRouter from './avue-router';
import i18n from '@/lang' // Internationalization
import Store from '../store/';
// import HelloWorld from '@/components/HelloWorld'
// import AxiosTest from '@/components/AxiosTest'

// const Foo = { template: '<div>foo</div>' }
// const Bar = { template: '<div>bar</div>' }

Vue.use(Router)

let Router1 = new Router({
    // 用户希望当我查看玩详情页以后返回，返回列表页的位置是刚刚浏览的位置
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            if (from.meta.keepAlive) {
                from.meta.savedPosition = document.body.scrollTop;
            }
            return {
                x: 0,
                y: to.meta.savedPosition || 0
            }
        }
    },
    routes: []
});
AvueRouter.install(Vue, Router1, Store, i18n);
Router1.$avueRouter.formatRoutes([], true);
Router1.addRoutes([...indexRouter]);
export default Router1;

// export default new Router({
//   routes: indexRouter
// })
// export default [
//     {
//       path: '/',
//       name: 'HelloWorld',
//       component: HelloWorld
//     },
//     {
//       path: '/foo',
//       name: 'foo',
//       component: Foo
//     },
//     {
//       path: '/bar',
//       name: 'bar',
//       component: Bar
//     },
//     {
//       path: '/AxiosTest',
//       name: 'AxiosTest',
//       component: AxiosTest
//     }
// ]