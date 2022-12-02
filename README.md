# java-either

The Either monad provides a means for modelling two types simultaneously,
acting as a binary summation type holding concrete (non-null) object instances.

## Why Use Either?

The Either type offers a couple of compelling use cases.

1. It can act as a generic return type holding equally valid values, preventing
   the need to make specialized result classes.
2. It can act as a rich Optional type, providing contextual information for the
   empty case.
3. It can act as an alternative means of exception handling that allows
   non-throwable signals and doesn't unwind the call stack.

The third use case in particular can lead to much more compact and readable
code, with the additional ability to delay error handling if needed. Compare
the following invocation&hellip;

```java
long id = ...;
Either.lift(DB::get).apply(id)
    .mapRight(Foo::getBar)
    .flatMapRight(Either.lift(Bar::toBaz))
    .consume(Exception::printStackTrace, Baz::show);
```
 
&hellip;with a more conventional try-catch solution.

```java
long id = ...;
try {
    Bar bar = DB.get(id).getBar();
    try {
        bar.toBaz().show();
    } catch (Exception e) {
        e.printStackTrace();
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

## Usage

[It's on Maven Central](https://central.sonatype.dev/artifact/io.github.Lindelt/java-either/1.0.0)

#### Maven
```xml
<dependency>
    <groupId>io.github.Lindelt</groupId>
    <artifactId>java-either</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle (Groovy)
```groovy
dependencies {
    implementation 'io.github.Lindelt:java-either:1.0.0'
}
```

#### Gradle (Kotlin)
```kotlin
dependencies {
    implementation("io.github.Lindelt:java-either:1.0.0")
}
```

### Example Usage
```java
String userInput = ...;

// get with lift-apply...
Either<Exception, Integer> parsed = Either.lift(Integer::parseInt)
    .apply(userInput);

// ...or get with supplier
parsed = Either.of(() -> Integer.parseInt(userInput));

// using checks
if (parsed.isRight()) {
    System.out.println(parsed.getRight() + " bottles of beer on the wall!");
} else {
    System.out.println(Objects.toString(userInput).toUpperCase());
}

// using map-consume
parsed.mapLeft(e -> Objects.toString(userInput).toUpperCase())
    .mapRight(i -> i + " bottles of beer on the wall!")
    .consume(System.out::println, System.out::println);

// using fold
System.out.println(parsed.fold(e -> Objects.toString(userInput).toUpperCase(),
    i -> i + " bottles of beer on the wall!"));

// "5"    -> 5 bottles of beer on the wall!
// "test" -> TEST
// null   -> NULL
// "0023" -> 23 bottles of beer on the wall!
// "0.14" -> 0.14
```