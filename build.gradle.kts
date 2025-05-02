defaultTasks("build", "shadowJar")

allprojects {
    version = (System.getenv("VERSION") ?: "dev")

    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_23.toString()
        targetCompatibility = JavaVersion.VERSION_23.toString()
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    if (project.name != "yueplugin-common-api") {
        dependencies {
            "compileOnly"(project(":yueplugin-common-api"))

            "compileOnly"(rootProject.libs.lombok)
            "annotationProcessor"(rootProject.libs.lombok)
        }
    }

    dependencies {
        "compileOnly"(rootProject.libs.yue)
        "compileOnly"(rootProject.libs.spring.boot)
        "compileOnly"(rootProject.libs.jda)
    }
}