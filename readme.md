# OucFly——基于Java的中国海洋大学教务助手

![rocket-launch](img/rocket-launch.png)

> #### **高性能、可拓展、功能齐全，全功率模式下开4线程爬取50人的信息只需1秒**

## 1.Jar包导入

### maven导入

```xml
<dependency>
    <groupId>cn.ouctechnology</groupId>
    <artifactId>oucfly</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### gradle导入

```java
repositories {
    mavenCentral()
    maven { url "https://raw.githubusercontent.com/zqx123321/OucFly/master/repository" }
}

dependencies {
    compile 'cn.ouctechnology:oucfly:1.0-SNAPSHOT'
}
```

### lib导入

直接导入目录下的oucfly.jar即可

## 2. 快速开始

OucFly的使用方式非常简单，仅仅需要短短的几行代码，就能实现一个个强大的功能。

例如获取一个人入学以来的所有成绩，只需要下面几行代码：

```java
//通过Builder来创建OucFly对象，传入学号和教务处密码
OucFly oucFly = OucFly.builder(userName, passWord).build();
//创建成绩详情功能单元
Operator operator = new GradeDetail(userCode);
//执行功能单元
Result result = oucFly.run(operator);
//判断是否成功
if (result.isSuccess()) {
    System.out.println(result.getContent());
}
```

再例如，获取一个人指定学期的课表，只需要将上述代码中的成绩详情功能单元GradeDetail换成课表的功能单元即可，其他代码完全一样：

```java
OucFly oucFly = OucFly.builder(userName, passWord)
        .build();
//指定2018年秋季学期
Operator operator = new ClassTable(userCode, new XnXq(2018, XnXq.Xq.AUTUMN));
Result result = oucFly.run(operator);
if (result.isSuccess()) {
    System.out.println(result.getContent());
}
```

下面，我将详细介绍每个功能模块。

