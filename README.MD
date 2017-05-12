## 九宫格解锁LockView介绍
##### 本项目为本人查阅网上资料实现的android下的九宫格锁屏，代码量较少 相对其他项目动辄近千行的代码，本项目代码仅三百余行，易学习易使用. #####


###  1.可以自定义宫格数量，在代码中更改Lockview中参数即可 ###
```
private final static int row = 3; //行数
private final static int colun = 3;//列数
```
### 2. 使用方法
1.导入项目

2.在XML中引用: 示例如下
```
<com.example.smaug.lockview.LockView
        android:id="@+id/lock_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"  />
```
3.在activty中设置监听即可

提供：setOnLockSuccessed\setOnLockError() 分别对应解锁成功与是失败；


### 3.效果预览 
![](https://github.com/que123567/LockVIew/blob/master/app/src/main/res/drawable/state_error.png)

![](https://github.com/que123567/LockVIew/blob/master/app/src/main/res/drawable/state_normal.png)

![](https://github.com/que123567/LockVIew/blob/master/app/src/main/res/drawable/state_wrongpassward.png)
