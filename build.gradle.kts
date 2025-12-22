defaultTasks("build", "shadowJar")

allprojects {
    version = (System.getenv("VERSION") ?: "dev")
    group = "me.whereareiam"

    apply(plugin = "java-library")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_25.toString()
        targetCompatibility = JavaVersion.VERSION_25.toString()
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.whereareiam.me/release")
        maven("https://maven.whereareiam.me/development")
    }

    if (project.name != "yuiverification-api") {
        dependencies {
            "compileOnly"(project(":yuiverification-api"))
        }
    }

    dependencies {
        // lombok
        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)

        // general
        "compileOnly"(rootProject.libs.yui)
    }
}