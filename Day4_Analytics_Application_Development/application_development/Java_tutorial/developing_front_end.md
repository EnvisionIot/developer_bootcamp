# Developing the Front End

After we have completed the back-end development of the web application, we can start the development of the front end for displaying the queried battery data. 



在开发完成后端代码后，还需要进行前端开发才能展示最终的效果，我们可以将已经开发好的前端文件直接解压到项目中。

1. 下载前端文件 Front End File.zip

2. 将前端文件解压到项目的resources目录下, 需要保证static，templeate目录与application.properties文件在同一目录下，最终目录结构如下：

   ```shell
   ├── src
   │   ├── main
   │   │   ├── java/
   │   │   └── resources/
   │   │       ├── application.properties
   │   │       ├── static/
   │   │       └── templates/
   ```

   

3. 开发一个展示前端页面的功能，选择controller目录，创建一个BatteryController类，输入以下代码

   ```java
   package com.example.batteryweb.controller;
   
   import lombok.extern.slf4j.Slf4j;
   import org.springframework.stereotype.Controller;
   import org.springframework.web.bind.annotation.RequestMapping;
   
   import javax.servlet.http.HttpServletRequest;
   import java.util.Map;
   
   @Controller
   @Slf4j
   public class BatteryController {
       @RequestMapping("/")
       public String chat(HttpServletRequest request, Map<String, String> data) {
           return "battery";
       }
   }
   ```

4. 最后，打开浏览器输入： http://127.0.0.1/ ，查看最终展示效果

   图片效果