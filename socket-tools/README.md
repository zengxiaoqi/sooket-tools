--springboot工程打包成一个jar包
mvn install -DskipTests -f pom.xml

--配置文件和可执行jar包分开
mvn install -DskipTests -f pom-pack.xml

--前后端打包成一个工程需要将前端代码build
--拷贝前端dist目录下的文件到resources目录下的static目录下