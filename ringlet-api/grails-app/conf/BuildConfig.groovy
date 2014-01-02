grails.servlet.version = "3.0"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolution = {
    inherits("global") {}
    log "error"
    checksums true
    legacyResolve false

    repositories {
        inherits true
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
    }

    plugins {
        build   ":tomcat:$grailsVersion"
        compile ":mongodb:1.3.0"
                ":rest:0.7"
                ":shiro:1.1.4"
                ":mail:1.0.1"
    }
}
