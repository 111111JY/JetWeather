# JetWeather_1
### 作者：危伟俊
### 时间：2018/8/6
### 程序名：JetWeather
### 描述：
***
本人的第一个GitHub开源软件，写得很烂。不过，学习嘛，总得一步一步慢慢来，每个人都是这样过来的，不喜勿喷，可以自行二次开发。该软件是基于郭霖大神的第一行代码实战二次开发的一个简而美的天气预报软件，具有定位城市、搜索城市、选择城市、管理城市、运动计步、每日更换背景图、语音唤醒播报天气信息、定时语音播报天气信息、下拉刷新、更换不同后台自动刷新频率、版本检测更新、天气消息提醒等功能。
***
使用说明：

版本更新功能在使用前，需要在服务器先放置一个版本信息JSON文件

`
{
  "version": {
           "Version_Name":"1.0",
           "Version_Code":"1"
            }
}`
***
根据这JSON文件的信息来决定是否需要版本更新。
代码中缺省了版本信息JSON文件地址和apk文件地址
悬浮按键不能随着屏幕的滑动而显示/隐藏
添加城市功能只能通过添加城市按钮以及定位按钮添加城市到城市列表中，第一次使用的定位并没有添加到城市列表，多
次定位同一个城市需要用户手动去删除多余的重复城市。
第一次定位界面页不能跳过，里面有做数据初始化，跳过后再进入软件会崩溃。但部分机型在此页面无法定位，该问题未解决。
***
**该软件为本人学习测试所有，有需要请联系本人。**
**微信  WWJ1436300433**
### 下面是一些界面截图：
![首页](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p1.png);
![开屏页](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p2.png);
![初始定位](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p3.png);
![侧滑栏](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p4.png);
![天气主页](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p5.png);
![更新频率](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p6.png);
![语音播报](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p7.png);
![关于](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p8.png);
![运动计步](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p9.png);
![训练计划](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p10.png);
![城市列表](https://github.com/111111JY/JetWeather_1/app/src/main/res/raw/images/p11.png);
