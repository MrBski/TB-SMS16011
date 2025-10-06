// Top-level build file
Tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
