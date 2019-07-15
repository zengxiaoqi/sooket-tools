1、编译：npm install
2、打包：npm run dist
3、打包完成之后，将 .env.js 和 masterTools拷贝到dist目录下的win-unpacked文件夹下，点击该文件夹下的exe程序运行
masterTools文件夹下的jar包由socket-tools工程打包拷贝过来。
socket-tools编译报错，需要增加参数 install -D skipTests -f pom.xml