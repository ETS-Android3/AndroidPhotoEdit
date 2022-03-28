图像编辑工具

问题:

1.Unresolved reference: BR


原因:在模块化过程中,使用DataBinding会出现这种情况


解决:在每个模块都需要添加kotlin-kapt配置以及设置dataBinding true

2.关于色温与白平衡

调节白平衡既为调整色温

3.关于裁剪框架无法加载问题
api 'com.theartofdev.edmodo:android-image-cropper:2.8.0'

settings.gradle需要添加镜像

maven { url 'https://maven.aliyun.com/repository/jcenter' }

