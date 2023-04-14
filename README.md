ShadowDB是基于SQL引擎技术的一套完整的针对全链路压测的开源数据分流系统，其基本思想是通过SQL解析、SQL路由对用户请求正确分流。
开发人员可以按照以下步骤进行测试。
## ShadowDB优势

- 支持6种关系型数据库系统中所有SQL语句的数据分流
- 实现了两种数据分流算法：基于列的影子算法和基于Hint的影子算法
- 实现了所有JDBC的接口
- 嵌入应用程序中，无需网络转发请求，对请求效率影响很小
## Github源代码
[https://github.com/Spatio-Temporal-Lab/shadow-db](https://github.com/Spatio-Temporal-Lab/shadow-db)
## 项目结构
本项目主要包括以下内容：

- ShadowDB 的主代码在jmh-base模块中。
- Takin 的主代码在jmh-takin模块中。
- Takin-web 的主代码在jmh-takin-web模块中。
- 以上每个模块中都包含了在Delete (de)、Update Index (ui)、Point Select (ps)、Read Only (ro)等不同场景下的Benchmark测试
- 测试Takin时需要额外添加的jar依赖在jar_package包中。
## 测试
### 先决条件
我们推荐IntelliJ IDEA来开发这个项目，需要下载并安装以下资源：

- Java 8 下载： [https://www.oracle.com/java/technologies/downloads/#java8](https://www.oracle.com/java/technologies/downloads/#java8)
- IntelliJ IDEA下载：[https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
- git 下载：[https://git-scm.com/download](https://git-scm.com/download)
- maven下载：[https://archive.apache.org/dist/maven/maven-3/](https://archive.apache.org/dist/maven/maven-3/)

下载并安装 jdk-8、IntelliJ IDEA 和 git。IntelliJ IDEA的maven项目附带maven，你也可以使用你自己的Maven环境，只需在设置中更改它。
### 克隆代码

1. 打开 _IntelliJ IDEA_，找到 _文件->新建->来自版本控制的项目_
2. 选择_仓库 URL_ ，在其界面中，_版本控制 _选择_ Git_
3. URL 填写：[https://github.com/Spatio-Temporal-Lab/shadow-db.git](https://github.com/Spatio-Temporal-Lab/shadow-db.git)
### 设置 JDK
文件 -> 项目结构 -> 项目 -> 项目 SDK -> 添加 SDK
单击 JDK 选择要下载 jdk-8 的地址
### 添加 jar包 依赖
文件 -> 项目结构 -> 模块 -> jmh-takin -> 依赖
单击"+" ，再点击Jar或目录，选择jar所在的地址
### 测试Takin
Takin系统如果要使用影子库技术，需采用网络转发的框架，在http请求里面添加相应标记。

- jmh-takin模块：Takin利用jdbc的，没有运用影子库技术，在运行时它的性能与原生MySQL基本一致。
- **jmh-takin-web模块**：运用了影子库技术，通过curl来发送http网络请求，因此，我们重点在于对jmh-takin-web包进行测试评估Takin的性能。


