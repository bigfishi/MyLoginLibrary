# MyLoginLibrary
login library with the third SDK.

- 执行 .\gradlew publishToMavenLocal 会在 用户文件夹下生成 pom文件，只是一个简单的xml文件，并没有打包
- 直接build，会在 build->outputs->arr 下面看到生成了aar包， app-debug.aar，这个就是生成的arr包，可以在其他项目中使用
- 在需要用到的项目中使用方法：
	- 将aar文件复制到模块的libs文件下
	- 在模块的build.gradle下面增加aar的引用
	```
	dependencies {
		...
	    // TODO  本地ARR测试
	    implementation (name: 'app-debug', ext: 'aar')
	    ...
    }
	```
	- 修改完build.gradle之后，sync一下，然后编写代码即可