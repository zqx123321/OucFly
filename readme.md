# OucFly——基于Java的中国海洋大学教务助手

<p align="center"><img width="200" src="img/rocket-launch.png" alt="Vue logo"></p>

<div align="center">
  <a href="https://github.com/zqx123321/OucFly/blob/master/"><img src="https://img.shields.io/github/license/zqx123321/OucFly.svg" alt="Build Status"></a>


  <a href="https://www.travis-ci.org/zqx123321/OucFly)"><img src="https://www.travis-ci.org/zqx123321/beauty.svg?branch=master" alt="Build Status"></a>


  <a href="https://github.com/zqx123321/OucFly#maven%E5%AF%BC%E5%85%A5"><img src="https://img.shields.io/badge/maven%20github-v1.0.0-brightgreen.svg" alt="Maven Github"></a>

</div>


> #### **高性能、可拓展、功能齐全，全功率模式下开4线程爬取50人的信息只需1秒**

## 1.Jar包导入

### maven导入

```xml
<dependency>
    <groupId>cn.ouctechnology</groupId>
    <artifactId>oucfly</artifactId>
    <version>1.0.0</version>
</dependency>
```

### gradle导入

```java
repositories {
    mavenCentral()
    maven { url "https://raw.githubusercontent.com/zqx123321/OucFly/master/repository" }
}

dependencies {
    compile 'cn.ouctechnology:oucfly:1.0.0'
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
OucFly oucFly = OucFly.builder(userName, passWord).build();
//指定2018年秋季学期
Operator operator = new ClassTable(userCode, new XnXq(2018, XnXq.Xq.AUTUMN));
Result result = oucFly.run(operator);
if (result.isSuccess()) {
    System.out.println(result.getContent());
}
```

下面，我将详细介绍每个功能模块。

## 3.OucFly对象

所有的功能单元必须都通过OucFly对象来执行，在程序运行过程中，可以保证OucFly对象是单例的，但是系统并不保证程序运行时间足够长，以致于cookie过期后，能自动获取新的cookie

### Builder对象

OucFly对象必须通过建造者Builder对象来创建，创建有两个必选参数userName和passWord，分别表示学号和教务处密码，此外，Builder还有工作模式和工作线程数两个可选参数

### 工作模式

教务处可用的入口有4个，分别是：

![1544359029359](D:\杂乱文件终章\oucfly\img\1544359029359.png)

与之相配合，OucFly提供了三种工作模式，由枚举类Mode指定，分别是：

![1544359144348](D:\杂乱文件终章\oucfly\img\1544359144348.png)

#### 全功率模式

全功率模式（FULL_POWER）同时利用了上述4个入口，效率最高

#### 校外模式

校外模式（OUTSIDE_ONLY）只利用了可以在校外访问的前两个入口

#### 自定义模式

自定义模式（ASSIGN_SELF）允许用于自己制定系统可用的入口

### 工作线程

OucFly默认只使用一个线程进行工作，当执行获取选课币或者院系排名等非常耗时的操作时，可以通过方法thread来指定多个线程，推荐线程数为运行系统的CPU数量，多了会增加线程切换的开销，得不偿失

### 创建举例

下面列举了常见的创建方式：

最简单：

```java
OucFly oucFly = OucFly.builder(userName, passWord)
        .build();
```

全功率多线程：

```java
OucFly oucFly = OucFly.builder(userName, passWord)
        .mode(Mode.FULL_POWER)
        .thread(4)
        .build();
```
指定Host：

```java
OucFly oucFly = OucFly.builder(userName, passWord)
        .addHost(Host.JWGL_OUC_EDU_CN)
        .addHost(Host.JWGL_2_OUC_EDU_CN)
        .build();
```

## 4. Operator对象

功能单元的总父类，所有的操作均是它的子类，所有功能组件的全家福如下：

![1544360041991](D:\杂乱文件终章\oucfly\img\1544360041991.png)

需要拓展功能的时候，只需要继承此类，实现run方法即可

此外，虽然鼓励采用面向接口编程的方式：

```java
Operator operator = new GradeDetail(userCode);
Result result = oucFly.run(operator);
```

但是在本系统中，更建议直接使用具体的类，而不是Opertor基类，因为这样能不会出现泛型的擦除，能切切实实地知道返回值的类型：

```java
GradeDetail operator = new GradeDetail(userCode);
Result<List<GradeDetailEntity>> result = oucFly.run(operator);
```

## 5. Result对象

所有功能操作的返回结果均是带有泛型的Result对象，Result对象中有如下字段，可以用来判断操作是否执行成功，获取操作执行结果以及错误信息等

![1544360264847](D:\杂乱文件终章\oucfly\img\1544360264847.png)

## 6. 获取院系列表

获取学院信息：

```java
//创建院系列表过滤器，必须指定年级
DeptFilter deptFilter = new DeptFilter(2015);
//创建工作单元
Dept dept = new Dept(deptFilter);
Result<List<DeptEntity>> run = oucFly.run(dept);
```

获取专业信息：

```java
//1、指定院系代码
DeptFilter deptFilter = new DeptFilter(2015)
        .setDept("0010");
//2、设置院系的名字，系统将自动寻找院系代码，支持模糊搜索
DeptFilter deptFilter = new DeptFilter(2015)
        .filterDept("信息");
//创建工作单元
Dept dept = new Dept(deptFilter);
Result<List<DeptEntity>> run = oucFly.run(dept);
```

获取班级信息：

```java
//1、指定班级代码
DeptFilter deptFilter = new DeptFilter(2015)
        .setDept("0010")
        .setMajor("0096");
//2、设置班级的名字，系统将自动寻找院系代码，支持模糊搜索
DeptFilter deptFilter = new DeptFilter(2015)
        .filterDept("信息")
        .filterMajor("保密");
//创建工作单元
Dept dept = new Dept(deptFilter);
Result<List<DeptEntity>> run = oucFly.run(dept);
```

返回值为DeptEntity的列表：

```java
@Data
public class DeptEntity {
    //院系名称
    private String name;
    //院系代码
    private String code;
}
```

## 7. 获取学生信息

此功能可以获取一个院系中的学生信息或者一门课中学生的信息，功能单元为Student，创建需要传入一个过滤器StudentFilter，StudenFilter是一个抽象类，它有两个子类：StudentDeptFilter和StudentClassFilter，分别表示获取院系学生信息和选课学生信息

获取院系中的学生信息：

```java
//创建院系过滤器
DeptFilter deptFilter = new DeptFilter(2015)
        .filterDept("信息")
        .filterMajor("保密");
//创建学生院系过滤器
StudentFilter filter = new StudentDeptFilter(deptFilter);
Student student = new Student(filter);
Result<List<StudentEntity>> result = oucFly.run(student);
```

获取院系中指定学号获取指定名字的学生信息：

```java
DeptFilter deptFilter = new DeptFilter(2015)
        .filterDept("信息")
        .filterMajor("保密");
//获取指定学号的学生信息
StudentFilter filter = new StudentDeptFilter(deptFilter)
        .filterCode(userCode);
//获取指定名字的学生信息
StudentFilter filter = new StudentDeptFilter(deptFilter)
        .filterName("张三");
Student student = new Student(filter);
Result<List<StudentEntity>> result = oucFly.run(student);
```

获取某一门课的选课学生名单：

```java
//创建StudentClassFilter，传入学年学期和8位的选课号
StudentFilter filter = new StudentClassFilter(
        new XnXq(2018, XnXq.Xq.AUTUMN), "13002100")
        .filterCode(userCode);

Student student = new Student(filter);
Result<List<StudentEntity>> result = oucFly.run(student);
```

返回为StudentEntity列表：

```java
@Data
public class StudentEntity {
    //年级
    private int grade;
    //学号
    private String code;
    //姓名
    private String name;
    //性别
    private String sex;
    //所在学院
    private String dept;
    //所在专业
    private String major;
}
```
