ga-tsp
======

![license](https://badgen.net/badge/license/MIT/orange)

_A TSP solver using genetic algorithm with mixed weighted operators._


Build
-----

This project uses [Java](https://www.oracle.com/java) and [Gradle](https://gradle.org). So check if you have locally installed Java at first. __Java 8 is required.__

If you have locally installed __Gradle 5__ and correctly configure environment variables, just execute the following command to complete building.

```console
$ gradle build
```

If you haven't installed Gradle, the start up script of gradle provides an easy way for you. Choose the specific method that suits your platform. Remember to enter the root directory of this project first.


### For Linux

```console
$ chmod +x ./gradlew
$ ./gradlew build
```

### For Windows

```console
> .\gradlew.bat build
```

Usage
-----

Now you get an executable jar archive `./build/libs/ga-tsp.jar`. You can run it with a command of the following form.

```console
$ java -jar ./build/libs/ga-tsp.jar [Parameters]
```

All calculation cases, got from [TSPLIB](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/), are solified in the program. To calculate one of them, you need to find out the index of the case you want.

```console
$ java -jar ./build/libs/ga-tsp.jar --cases
```

For example, the index of the TSP ch130 is 5. You can perform a calculation on it by specifying the index equal to 5 and the number of times equal to 1.

```console
$ java -jar ./build/libs/ga-tsp.jar --test 5 --times 1
```

Logs and images will be saved in `logs` and `imgs`, respectively.

The following is all parameters that you can use and their meanings.

Parameter         | Meaning
-                 | -
--help            | Get the help manual.
--cases           | Get all test cases with their order number.
--test all        | Indicate that all cases should be tested.
--test Number     | Designate the case to be tested.
--times Number    | Designate the number of calculation time. Default value is 10.
--size Number     | Designate the size of population. Default value is 35.
--pc Number       | Designate the probability of crossover. Default value is 0.96.
--pm Number       | Designate the probability of mutation. Default value is 0.66.
--topx Number     | Designate the number of individuals to remain before every crossover. Default value is 2.
--topy Number     | Designate the number of individuals to remain before every mutation. Default value is 5.
--topz Number     | Designate the number of individuals to remain before every selection. Default value is 3.
--queue Number    | Designate the number of best individuals maintained in the priority queue while in evolution. Default value is 500.
--leastgen Number | Designate the number of generations which means evolution should iterate for at least so many generations. Default value is 3000.
--limitgen Number | Designate the number of generations which means evolution should'nt iterate for more than so many generations. Default value is 10000.
--stay Number     | Designate the number of generations which means the best obtained individual should stay in the priority queue for at least so many generations. Default value is 1000.
--diff Number     | Designate the difference which means the difference between the fitness of best individual and that of the worst one should not be greater than this value. Default value is 1e-7.


Maintainer
----------

[@pursuemoon](https://github.com/pursuemoon)


License
-------

[MIT](LICENSE) © pursuemoon. 
