ga-tsp
======

![license](https://badgen.net/badge/license/MIT/orange)

_一个采用遗传算法和混合带权算子的旅行商问题求解器。_


构建
----

本项目使用了[Java](https://www.oracle.com/java)与[Gradle](https://gradle.org)。所以请先确保你已在本地安装好了Java，__要求使用Java 8。__

如果你已在本地安装好了__Gradle 5__并且正确地配置好了环境变量，那么只需要执行下面这行命令就能完成构建。

```console
$ gradle build
```

如果你还没安装Gradle，Gradle的启动脚本也为你提供了一种简单的方式，根据你自己的平台在下面选择一个适合你的方法，记得先进入项目的根目录。

### Linux

```console
$ chmod +x ./gradlew
$ ./gradlew build
```

### Windows

```console
> .\gradlew.bat build
```

使用方法
--------

现在你已经有了一个可执行的jar包`./build/libs/ga-tsp.jar`，通过下面形式的命令来运行它即可。

```console
$ java -jar ./build/libs/ga-tsp.jar [启动参数]
```

所有的算例均来自[TSPLIB](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/)，它们被固化在程序中，通过检索它们对应的索引，以此来实现对某一个算例的计算。

```console
$ java -jar ./build/libs/ga-tsp.jar --cases
```

打个比方，旅行商问题ch130的索引是5，你可以通过指定问题索引为5，计算次数为1来对它进行1次计算。

```console
$ java -jar ./build/libs/ga-tsp.jar --test 5 --times 1
```

日志和图片会分别保存在`logs`和`imgs`中。

所有可用的启动参数以及对应的含义如下。

启动参数          | 含义
----              | ----
--help            | 获取帮助手册。
--cases           | 获取测试算例与对应的索引号。
--test all        | 测试所有的算例。
--test Number     | 测试特定的算例。
--times Number    | 指定计算次数，默认值为10。
--size Number     | 指定种群规模，默认值为35。
--pc Number       | 指定交叉概率，默认值为0.96。
--pm Number       | 指定变异概率，默认值为0.66。
--topx Number     | 指定交叉前的精英保留数目，默认值为2。
--topy Number     | 指定变异前的精英保留数目，默认值为5。
--topz Number     | 指定选择前的精英保留数目，默认值为3。
--queue Number    | 指定最优个体队列的容量，默认值为500。
--leastgen Number | 指定至少需要演化的世代数，默认值为3000。
--limitgen Number | 指定演化世代数的上限，到达该上限则停止演化，默认值为10000。
--stay Number     | 指定停机前最优个体在最优个体队列里至少应停留的世代数，默认值是1000。
--diff Number     | 指定停机前最优个体队列中的最优个体与最差个体的适应度差值上限，默认值是1e-7。


维护者
------

[@pursuemoon](https://github.com/pursuemoon)


使用许可
--------

[MIT](LICENSE) © pursuemoon. 
