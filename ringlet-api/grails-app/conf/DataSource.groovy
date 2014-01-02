environments {

    development {
        grails {
            mongo {
                port = 27107
                databaseName = "ringlet-dev"
                options {
                    autoConnectRetry = true
                    connectTimeout = 300
                }
            }
        }
    }

    test {
        grails {
            mongo {
                port = 27107
                databaseName = "ringlet-test"
                options {
                    autoConnectRetry = true
                    connectTimeout = 300
                }
            }
        }
    }

    production {
        grails {
            mongo {
                port = 27107
                username = "admin"
                password = "frankdog007"
                databaseName = "ringlet-prod"
                options {
                    autoConnectRetry = true
                    connectTimeout = 300
                }
            }
        }
    }
}