## 准备工作

```bash
# 安装
$ brew install protoc

# 查看版本
$ protoc --version
libprotoc 3.21.12
```

## 编译命令

```bash
#生成message相关信息
yingtao@yingtaodeMacBook-Pro grpc-demo % pwd                                                            
/Users/yingtao/IdeaProjects/demo-summary/grpc-demo
yingtao@yingtaodeMacBook-Pro grpc-demo % ls
README.md       gretter.proto   pom.xml         src
yingtao@yingtaodeMacBook-Pro grpc-demo % protoc --proto_path=. --java_out=./src/main/java/ gretter.proto
#生成service相关信息
#1.从 https://repo.maven.apache.org/maven2/io/grpc/protoc-gen-grpc-java/ 下载
#  protoc-gen-grpc-java-1.56.1-osx-x86_64.exe，下载完成后去掉exe后缀（exe虽然去掉了，但是终端中仍然显示后缀）
#2.修改权限（不修改chmod会报错）
yingtao@yingtaodeMacBook-Pro grpc-demo % chomd +x protoc-gen-grpc-java-1.56.1-osx-x86_64.exe 
#3.生成service相关的类（包括服务端的抽象类PlatformImplBase以及客户端的PlatformBlockingStub类）
yingtao@yingtaodeMacBook-Pro grpc-demo % pwd                                       
/Users/yingtao/IdeaProjects/demo-summary/grpc-demo
yingtao@yingtaodeMacBook-Pro grpc-demo % ls
README.md                                       pom.xml                                         src
gretter.proto                                   protoc-gen-grpc-java-1.56.1-osx-x86_64.exe
yingtao@yingtaodeMacBook-Pro grpc-demo % protoc --plugin=protoc-gen-grpc-java=./protoc-gen-grpc-java-1.56.1-osx-x86_64.exe --grpc-java_out=./src/main/java/ gretter.proto
---------------------------------------------------------------------------------------------------
$ protoc --help
Usage: protoc [OPTION] PROTO_FILES

  -IPATH, --proto_path=PATH   指定搜索路径
  --plugin=EXECUTABLE:
  
  ....
 
  --cpp_out=OUT_DIR           Generate C++ header and source.
  --csharp_out=OUT_DIR        Generate C# source file.
  --java_out=OUT_DIR          Generate Java source file.
  --js_out=OUT_DIR            Generate JavaScript source.
  --objc_out=OUT_DIR          Generate Objective C header and source.
  --php_out=OUT_DIR           Generate PHP source file.
  --python_out=OUT_DIR        Generate Python source file.
  --ruby_out=OUT_DIR          Generate Ruby source file
  
   @<filename>                proto文件的具体位置
```

编译完成后文件路径如下：

<img src="https://typora-imagehost-1308499275.cos.ap-shanghai.myqcloud.com/2023-6/image-20230727111744765.png" alt="image-20230727111744765" style="zoom:50%;" />

## 依赖

依赖版本与protoc版本对应，否则可能生成的代码中部分库爆红

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>${grpc.version}</version>
</dependency>

<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc.version}</version>
</dependency>

<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc.version}</version>
</dependency>

<dependency> <!-- necessary for Java 9+ -->
    <groupId>org.apache.tomcat</groupId>
    <artifactId>annotations-api</artifactId>
    <version>6.0.53</version>
    <scope>provided</scope>
</dependency>
```

