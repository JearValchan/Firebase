package armando.valencia.menudecomidas.dummy

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Comida> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Comida> = HashMap()

    private val COUNT = 0

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    fun addItem(item: Comida) {
        ITEMS.add(item)
        ITEM_MAP.put(item.getId(), item)
    }

    fun updateItem(item:Comida){
        ITEMS.set(ITEMS.indexOf(item), item)
        ITEM_MAP.put(item.getId(), item)
    }

    fun deleteItem(item:Comida){
        ITEMS.remove(item)
        ITEM_MAP.remove(item)
    }

    private fun createDummyItem(position: Int): Comida {
        return Comida(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    fun getComida(name:String): Comida? {
        for (comida:Comida in ITEMS){
            if (comida.getName().equals(name)){
                return comida
            }
        }
        return null
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class Comida(private var id: String,private var name: String,private var price: String) {

        constructor() : this("","","") {

        }

        constructor(nombre:String, precio:String):this("", nombre, precio){
            this.name = nombre
            this.price = precio
        }

        fun getId():String{
            return this.id
        }

        fun setId(id: String?){
            this.id = id!!
        }

        fun getName():String{
            return this.name
        }

        fun setName(name: String?){
            this.name = name!!
        }

        fun getPrice():String{
            return this.price
        }

        fun setPrice(price: String?){
            this.price = price!!
        }

        override fun toString(): String = name

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Comida

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }
}
