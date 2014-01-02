package com.ps.ringlet

class Picture {

    String path
    boolean isPublic

    static constraints = {
        path nullable: true
        isPublic nullable: true
    }

    def toObject(){
        return [id: this.id,
                path: this.path,
                isPublic: this.isPublic]
    }
}
