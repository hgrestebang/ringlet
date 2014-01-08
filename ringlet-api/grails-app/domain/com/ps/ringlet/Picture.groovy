package com.ps.ringlet

class Picture {

    String path

    static constraints = {
        path nullable: true
    }

    def toObject(){
        return [id: this.id,
                path: this.path]
    }
}