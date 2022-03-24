问题:

1.Unresolved reference: BR


原因:在模块化过程中,使用DataBinding会出现这种情况


解决:在每个模块都需要添加kotlin-kapt配置以及设置dataBinding true
