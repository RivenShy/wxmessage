    var data4Vue = {
            heros: [],
            hero4Add: { id: 0, name: '', hp: '0'},
            pagination:{}
    };

var vue = new Vue({
    // Vue 对象加载成功
    el:"#content",
    data:{
      wxQrCodeurl:'',
      shopShow: false,
    },
    mounted:function() {
//        alert("Hello Vue!")
    },
    methods: {
        bindNow: function(event) {
            var url = "/wx/getQRcode";
            axios.get(url).then(function(response) {
                if(response.data.code == 200) {
                    if(response.data.success == true) {
                        var qrCodeUrl = response.data.data;
                        vue.wxQrCodeurl = qrCodeUrl;
                        console.log(qrCodeUrl);
                        vue.shopShow = !vue.shopShow;
                    } else{
                        alert(response.data.msg);
                    }
                } else{
                    alert("请求失败");
                }
            })
        },
        scaned: function(event) {
//            alert("scaned");
            window.location.href = "/wx/listBindUserPage";
//            var url = "/wx/listBindUserPage";
//            axios.get(url).then(function(response) {
//
//            })
        },
    }
});
