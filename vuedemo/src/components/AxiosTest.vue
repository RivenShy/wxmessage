<template>
    <div>
        <h1>网站列表</h1>
        <div v-for="site in infos" >
        {{ site.name }}
        </div>
    </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'AxiosTest',
  data () {
    return {
      infos: null
    }
  },
  mounted () {
    axios.get('/api/try/ajax/json_demo.json')
      .then(response => {
        console.log(response.data.sites)
        this.infos = response.data.sites
      }
      ).catch(function (error) { // 请求失败处理
        console.log(error)
      })
  }
}
</script>
