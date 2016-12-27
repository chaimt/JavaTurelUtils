

## MDC - Stack

Logback has a very nice feature called Mapped Diagnostic Context ([MDC](http://logback.qos.ch/manual/mdc.html) in short).

According to the manual what it does is:

"It lets the developer place information in a *diagnostic context* that can be subsequently retrieved by certain logback components"

This is very useful for adding additional information to your logs without the overhead of formating. For example (also from the manual):

```java
MDC.put("first", "Dorothy");
Logger logger = LoggerFactory.getLogger(SimpleMDC.class);
MDC.put("last", "Parker");
logger.info("Check enclosed.");
```
What this did was add attributes to the log that include the first and last name. In the logger you can then specify how these attributes will be used:

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
 <layout>
   <Pattern>**%X{first} %X{last}** - %m%n</Pattern>
 </layout>
</appender>
```
# **The issue**

The MDC class is a static class that sits on the local thread so you can call from any place in your code to the class.

Since this is a static class, you must remember after you send your log to remove the attribute with the remove method.

No starts the read issue. What happens if you do not remove the attributes that you added? What will happen is that your thread will be reused and the next log that is sent will have the leftover tag on it.

So how do you make sure that you remove all tags?

Another issue how do you solve the nested issue. If I have a method that calls another method, and both set attributes what will happen? Well it depends on what you did. If the inner method just adds but did not remove then you will have the extra attribute. On the other hand if the inner methods updates the same attribute then you are stuck with the new value and not yours.

# **Solution**

The solution to the problem is to save the stacks state per method call. To do this I have created a utility MDCStack that saves on the local thread a list of all attributes and values. Every time you want to call a inner methods you need to call push on the stack, and when you leave the method you call pop. With these methods we will solve all the issues we raised above.

You do not need to remember all attributes that you added, you just need to call pop at the end. In addition if you are nesting methods by calling the pop, the stack will return the MDC state back to the same state that was before the call including returning original values.

## **Code**

```java
public class MDCStack {

  static public class MDCData {
      String key;
      Object value;

      public MDCData(String key, Object value) {
          this.key = key;
          this.value = value;
      }
  }

 static ThreadLocal<Stack<List<MDCData>>> keys = new ThreadLocal<Stack<List<MDCData>>>(){
      @Override protected Stack<List<MDCData>> initialValue() {
          final Stack stack = new Stack();
          stack.add(new ArrayList<>());
          return stack;
      }
  };

  static protected void rebuild(){
      final Stack<List<MDCData>> lists = keys.get();
      lists.forEach(mdcDatas -> {
          mdcDatas.forEach(mdcData -> MDC.put(mdcData.key,mdcData.value));
      });
  }

  static public void push(){
      final Stack<List<MDCData>> lists = keys.get();
      lists.add(new ArrayList<>());
  }

  static public void pop(){
      final Stack<List<MDCData>> lists = keys.get();
      if (lists.size()>0) {
          final List<MDCData> poll = lists.pop();
          poll.forEach(d -> MDC.remove(d.key));
          rebuild();
      }

      if (lists.size()==0){
          lists.add(new ArrayList<>());
      }
  }

  static public void put(String key, Object value){
      final Stack<List<MDCData>> lists = keys.get();
      final List<MDCData> peek = lists.peek();
      peek.add(new MDCData(key,value));
      MDC.put(key,value);
  }

  static public void remove(String key){
      final Stack<List<MDCData>> lists = keys.get()
      final List<MDCData> peek = lists.peek();
      peek.stream()
              .filter(mdcData -> mdcData.key.equals(key))
              .findFirst()
              .ifPresent(mdcData -> peek.remove(mdcData));
      MDC.remove(key);
  }

  static public void clear(){
      MDC.clear();
      final Stack<List<MDCData>> lists = keys.get();
      lists.clear();
      lists.add(new ArrayList<>());
  }
}
```

All code can be found at:

[https://github.com/chaimt/TurelUtils/blob/master/src/main/java/com/turel/utils/MDCStack.java](https://github.com/chaimt/TurelUtils/blob/master/src/main/java/com/turel/utils/MDCStack.java)

[https://github.com/chaimt/TurelUtils/blob/master/src/test/java/com/turel/utils/MDCStackTest.java](https://github.com/chaimt/TurelUtils/blob/master/src/test/java/com/turel/utils/MDCStackTest.java)


