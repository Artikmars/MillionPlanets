package com.artamonov.millionplanets.model

class Item {

    constructor()
    constructor(itemId: Long?, itemAmount: Long?) {
        this.itemId = itemId
        this.itemAmount = itemAmount
    }

    constructor(itemName: String?) {
        this.itemName = itemName
    }

    var itemId: Long? = null
    var itemAmount: Long? = null
    var itemName: String? = null
}
